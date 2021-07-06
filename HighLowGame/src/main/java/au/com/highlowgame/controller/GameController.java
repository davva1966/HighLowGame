package au.com.highlowgame.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.dbcp2.BasicDataSource;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import au.com.highlowgame.model.Answer;
import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameParticipant;
import au.com.highlowgame.model.GameParticipantAnswer;
import au.com.highlowgame.model.GameStatus;
import au.com.highlowgame.model.GameTracker;
import au.com.highlowgame.model.GameType;
import au.com.highlowgame.model.Player;
import au.com.highlowgame.model.Question;
import au.com.highlowgame.model.validate.GameValidator;
import au.com.highlowgame.service.ReportService;
import au.com.highlowgame.service.TranslationService;
import au.com.highlowgame.user.UserContextService;
import au.com.highlowgame.util.ApplicationContextProvider;
import au.com.highlowgame.util.RedirectHelper;
import au.com.highlowgame.util.SSUtil;

@RequestMapping("/games")
@Controller
public class GameController {

	public static String POST_QUESTION = "POST_QUESTION";
	public static String FIRST_ANSWER = "FIRST_ANSWER";

	@Autowired
	BasicDataSource baseDataSource;

	@Autowired
	TranslationService translationService;

	@Autowired
	RedirectHelper redirectHelper;

	@Autowired
	ReportService reportService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		GameValidator validator = ApplicationContextProvider.getApplicationContext().getBean(GameValidator.class);
		validator.setEmbeddedValidator(binder.getValidator());
		binder.setValidator(validator);
	}

	@RequestMapping(params = "form1", produces = "text/html")
	public String create1Form(Model uiModel) {
		Game game = new Game();
		game.setOwner(UserContextService.getCurrentPlayer());
		game.setGameType(GameType.ALLPLAYERS);
		game.setGameLeader(UserContextService.getCurrentPlayer());
		game.setGameStatus(GameStatus.CREATED);
		populateEditForm1(uiModel, game);

		return "games/create1";
	}

	@RequestMapping(params = "form2", produces = "text/html")
	public String create2Form(Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		populateEditForm2(uiModel, game);
		return "games/create2";
	}

	void populateEditForm1(Model uiModel, Game game) {
		uiModel.addAttribute("game", game);
		uiModel.addAttribute("gameTypeSelections", GameType.getAll());
		addDateTimeFormatPatterns(uiModel);

		Player gameLeader = game.getGameLeader();
		if (gameLeader != null)
			uiModel.addAttribute("players", SSUtil.toList(gameLeader));
		else
			uiModel.addAttribute("players", SSUtil.toList(new Player()));
	}

	void populateEditForm2(Model uiModel, Game game) {
		uiModel.addAttribute("game", game);

		List<String> playerIds = new ArrayList<String>();
		List<Player> players = new ArrayList<Player>();

		// If leader lead game, skip leader
		if (game.getGameType() == GameType.LEADER && game.getGameLeader() != null)
			playerIds.add(game.getGameLeader().getId());

		// Add players already in the game
		if (SSUtil.notEmpty(game.getParticipants())) {
			for (Player player : game.getPlayers()) {
				// If leader lead game skip leader
				if (game.getGameType() == GameType.LEADER && game.getGameLeader() != null && game.getGameLeader().equals(player))
					continue;
				else
					player.setSelected(true);
				playerIds.add(player.getId());
				players.add(player);
			}
		}

		// Add all other players
		players.addAll(Player.findAllPlayersExcept(playerIds));
		uiModel.addAttribute("players", players);

		addDateTimeFormatPatterns(uiModel);

	}

	void populateAnswerQuestionForm(Model uiModel, GameParticipantAnswer answer) {
		uiModel.addAttribute("answer", answer);
		uiModel.addAttribute("highLowAnswerSelections", Answer.getAll());
		addDateTimeFormatPatterns(uiModel);
	}

	@RequestMapping(value = "update1", produces = "text/html")
	public String update1(@Valid Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm1(uiModel, game);
			return "games/update1";
		}

		game.merge();
		uiModel.asMap().clear();
		return update2Form(game, bindingResult, uiModel, request);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update2", produces = "text/html")
	public String update2(@Valid Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm2(uiModel, game);
			return "games/update2";
		}

		List<GameParticipant> selectedParticipants = new ArrayList<GameParticipant>();
		Enumeration<String> parmNames = request.getParameterNames();
		while (parmNames.hasMoreElements()) {
			String parmName = parmNames.nextElement();
			if (parmName.startsWith("selection_checkbox_")) {
				String parmValue = request.getParameter(parmName);
				if (parmValue.equals("") == false) {
					int idx = parmName.lastIndexOf("_");
					String playerId = parmName.substring(idx + 1);
					Player player = Player.find(playerId);
					if (player != null) {
						GameParticipant participant = new GameParticipant();
						participant.setGame(game);
						participant.setPlayer(player);
						participant.setPoints(game.getStartingPoints());
						selectedParticipants.add(participant);
					}
				}
			}

		}

		if (game.getGameType() == GameType.LEADER && selectedParticipants.size() == 0) {
			uiModel.addAttribute("errorMessage", translationService.translate("message_com_ss_highlowgame_atleastoneplayersmustbeselected"));
			populateEditForm2(uiModel, game);
			return "games/create2";
		} else if (game.getGameType() == GameType.ALLPLAYERS && selectedParticipants.size() < 2) {
			uiModel.addAttribute("errorMessage", translationService.translate("message_com_ss_highlowgame_atleasttwoplayersmustbeselected"));
			populateEditForm2(uiModel, game);
			return "games/create2";
		}

		game.removeAllParticiants();
		game.setParticipants(selectedParticipants);
		game.merge();

		uiModel.asMap().clear();

		return "redirect:/games/" + encodeUrlPathSegment(game.getId().toString(), request);
	}

	@RequestMapping(value = "/create1", produces = "text/html")
	public String create1(@Valid Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm1(uiModel, game);
			return "games/create1";
		}

		game.persist();

		uiModel.asMap().clear();
		return create2Form(game, bindingResult, uiModel, request);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/create2", produces = "text/html")
	public String create2(@Valid Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateEditForm2(uiModel, game);
			return "games/create2";
		}

		List<GameParticipant> selectedParticipants = new ArrayList<GameParticipant>();
		Enumeration<String> parmNames = request.getParameterNames();
		while (parmNames.hasMoreElements()) {
			String parmName = parmNames.nextElement();
			if (parmName.startsWith("selection_checkbox_")) {
				String parmValue = request.getParameter(parmName);
				if (parmValue.equals("") == false) {
					int idx = parmName.lastIndexOf("_");
					String playerId = parmName.substring(idx + 1);
					Player player = Player.find(playerId);
					if (player != null) {
						GameParticipant participant = new GameParticipant();
						participant.setGame(game);
						participant.setPlayer(player);
						participant.setPoints(game.getStartingPoints());
						selectedParticipants.add(participant);
					}
				}
			}

		}

		if (game.getGameType() == GameType.LEADER && selectedParticipants.size() == 0) {
			uiModel.addAttribute("errorMessage", translationService.translate("message_com_ss_highlowgame_atleastoneplayersmustbeselected"));
			populateEditForm2(uiModel, game);
			return "games/create2";
		} else if (game.getGameType() == GameType.ALLPLAYERS && selectedParticipants.size() < 2) {
			uiModel.addAttribute("errorMessage", translationService.translate("message_com_ss_highlowgame_atleasttwoplayersmustbeselected"));
			populateEditForm2(uiModel, game);
			return "games/create2";
		}

		game.setParticipants(selectedParticipants);
		game.merge();

		uiModel.asMap().clear();
		return redirectHelper.redirectToEntityList("/games", 1, null, request);
	}

	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("game", game);
		uiModel.addAttribute("itemId", id);

		// Connection con = null;
		// try {
		// con = baseDataSource.getConnection();
		//
		// JasperReportBuilder reportBuilder;
		// try {
		// reportBuilder = reportService.getGameReportBuilder(game, con);
		// } catch (Exception e) {
		// return "games/show";
		// }
		//
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		//
		// String uuid = UUID.randomUUID().toString();
		//
		// JasperHtmlExporterBuilder htmlExporter = export.htmlExporter(out).setImagesURI("image?uuid=" + uuid + "&image=");
		// htmlExporter.setHtmlHeader("<//br>");
		// htmlExporter.setHtmlFooter("<//br>");
		//
		// request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, reportBuilder.toJasperPrint());
		// reportBuilder.toHtml(htmlExporter);
		//
		// String html = out.toString("UTF-8");
		//
		// out.close();
		//
		// uiModel.addAttribute("items", new ArrayList<>());
		// uiModel.addAttribute("reportHtml", html);
		// } catch (Exception e) {
		// } finally {
		// try {
		// if (con != null)
		// con.close();
		// } catch (Exception e2) {
		// }
		// }

		return "games/show";
	}

	@RequestMapping(value = "startgame/{id}", produces = "text/html")
	public String startGame(@PathVariable("id") String id, Model uiModel, RedirectAttributes redirectAttrs, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, HttpServletRequest request) {

		if (page == null)
			page = 1;
		if (size == null)
			size = 50;

		Game game = Game.find(id);
		if (game == null)
			return redirectHelper.redirectToEntityList("/games", page, size, request);

		try {
			game.start();
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("infoMessage", translationService.translate(e));
			return redirectHelper.redirectToEntityList("/games", page, size, request);
		}

		Player currentPlayer = UserContextService.getCurrentPlayer();
		if (game.hasParticipant(currentPlayer) || game.getGameLeader().equals(currentPlayer)) {
			if (game.hasParticipant(currentPlayer))
				game.playerJoined(currentPlayer);
			return "redirect:/games/waitforplayers/" + encodeUrlPathSegment(game.getId().toString(), request);
		} else {
			return redirectHelper.redirectToEntityList("/games", page, size, request);
		}
	}

	@RequestMapping(value = "joingame/{id}", produces = "text/html")
	public String joinGame(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game != null) {
			Player currentPlayer = UserContextService.getCurrentPlayer();
			if (currentPlayer != null)
				game.playerJoined(currentPlayer);
		}

		return waitForPlayersToJoin(id, uiModel, request);
	}

	@RequestMapping(value = "declinegame/{id}", produces = "text/html")
	public String declinegame(@PathVariable("id") String id, @RequestParam(value = "participant", required = false) String participantId, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game != null) {
			GameParticipant participant = GameParticipant.find(participantId);
			if (participant != null)
				game.removeParticipant(participant);
		}

		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("game", game);
		uiModel.addAttribute("itemId", id);
		uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_invitationdeclined"));

		return "games/declined";
	}

	@RequestMapping(value = "finishgame/{id}", produces = "text/html")
	public String finishgame(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game != null)
			game.finish();

		uiModel.asMap().clear();
		return redirectHelper.redirectToEntityList("/games", page, size, request);
	}

	@RequestMapping(produces = "text/html")
	public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
		if (page != null || size != null) {
			int sizeNo = size == null ? 50 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			uiModel.addAttribute("games", Game.findGameEntries(firstResult, sizeNo));
			float nrOfPages = (float) Game.countGames() / sizeNo;
			uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
		} else {
			uiModel.addAttribute("games", Game.findAllGames());
		}
		addDateTimeFormatPatterns(uiModel);

		return "games/list";
	}

	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String update1Form(@PathVariable("id") String id, Model uiModel) {
		populateEditForm1(uiModel, Game.find(id));
		return "games/update1";
	}

	@RequestMapping(value = "/update2", params = "form2", produces = "text/html")
	public String update2Form(Game game, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		populateEditForm2(uiModel, game);
		return "games/update2";
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game != null) {

			if (game.getGameStatus() == GameStatus.ACTIVE) {
				uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_game_cannotdeleteactivegame"));
				return show(id, uiModel, request);
			}

			game.remove();
		}

		uiModel.asMap().clear();
		return redirectHelper.redirectToEntityList("/games", page, size, request);
	}

	@RequestMapping(value = "waitforplayerstojoin/{id}", produces = "text/html")
	public String waitForPlayersToJoin(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		GameTracker tracker = game.getGameTracker();
		Player currentPlayer = UserContextService.getCurrentPlayer();

		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("game", game);

		if (game.allPlayersJoined()) {
			if (game.getGameType() == GameType.LEADER) {
				if (currentPlayer.equals(game.getGameLeader())) {
					if (tracker.getCurrentQuestion() == null) {
						Question question = Question.newQuestionForGame(game, null);
						return postQuestion1(question, uiModel);
					} else {
						return waitForPlayersToAnswer(id, uiModel, request);
					}
				}
			} else if (game.getGameType() == GameType.ALLPLAYERS) {
				if (tracker.getCurrentQuestion() == null) {
					GameParticipant first = game.findFirstParticipant();
					if (currentPlayer.equals(first.getPlayer())) {
						Question question = Question.newQuestionForGame(game, first);
						tracker.setParticipantPostingCurrentQuestion(first);
						tracker.merge();
						return postQuestion1(question, uiModel);
					}
				} else {
					if (currentPlayer.equals(tracker.getParticipantPostingCurrentQuestion().getPlayer()) || tracker.hasAnswered(currentPlayer)) {
						return waitForPlayersToAnswer(id, uiModel, request);
					} else {
						if (tracker.hasAnswered(tracker.getParticipantToAnswerFirst().getPlayer()))
							return answerQuestionForm(tracker.getCurrentQuestion().getId(), uiModel);
						else if (tracker.getParticipantToAnswerFirst() != null)
							return waitForPlayerAction(id, FIRST_ANSWER, uiModel, request);
					}
				}
			}
			if (tracker.hasPlayerPostedQuestion()) {
				if (currentPlayer.equals(tracker.getParticipantToAnswerFirst().getPlayer()))
					return answerQuestionForm(id, uiModel);
				else
					return waitForPlayersToAnswer(id, uiModel, request);
			} else {
				return waitForPlayerAction(id, POST_QUESTION, uiModel, request);
			}
		} else {
			List<String> allParticipantIds = game.findAllParticipantIds();
			uiModel.addAttribute("allParticipantIds", SSUtil.formatListToSeparated(allParticipantIds, ","));
			List<String> joinedParticipantIds = game.findJoinedParticipantIds();
			uiModel.addAttribute("joinedParticipantIds", SSUtil.formatListToSeparated(joinedParticipantIds, ","));
			return "games/waitforplayerstojoin";
		}
	}

	@RequestMapping(value = "waitforplayeraction/{id}", produces = "text/html")
	public String waitForPlayerAction(@PathVariable("id") String id, @RequestParam(value = "actionToWaitFor", required = false) String actionToWaitFor, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		GameTracker tracker = game.getGameTracker();

		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("game", game);
		uiModel.addAttribute("actionToWaitFor", actionToWaitFor);
		uiModel.addAttribute("hasFirstQuestionPoster", tracker.getParticipantPostingCurrentQuestion() != null);
		uiModel.addAttribute("hasFirstAnswerer", tracker.getParticipantToAnswerFirst() != null);

		if (actionToWaitFor.equalsIgnoreCase(POST_QUESTION) && tracker.hasPlayerPostedQuestion()) {
			return "games/waitforplayerstoanswer";
		} else if (actionToWaitFor.equalsIgnoreCase(FIRST_ANSWER) && tracker.hasAnswered(tracker.getParticipantToAnswerFirst().getPlayer())) {
			return answerQuestionForm(tracker.getCurrentQuestion().getId(), uiModel);
		} else {
			if (actionToWaitFor.equalsIgnoreCase(POST_QUESTION))
				if (tracker.getParticipantPostingCurrentQuestion() != null)
					uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_waitingforplayertopostquestion1", tracker.getParticipantPostingCurrentQuestion().getPlayer().getName()));
				else
					uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_waitingforplayertopostquestion2"));
			else if (actionToWaitFor.equalsIgnoreCase(FIRST_ANSWER))
				if (tracker.getParticipantToAnswerFirst() != null)
					uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_waitingforfirstanswer1", tracker.getParticipantToAnswerFirst().getPlayer().getName()));
				else
					uiModel.addAttribute("infoMessage", translationService.translate("message_com_ss_highlowgame_waitingforfirstanswer2"));
			return "games/waitforplayeraction";
		}

	}

	@RequestMapping(value = "waitforplayerstoanswer/{id}", produces = "text/html")
	public String waitForPlayersToAnswer(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		GameTracker tracker = game.getGameTracker();
		Player currentPlayer = UserContextService.getCurrentPlayer();
		Question currentQuestion = tracker.getCurrentQuestion();

		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("game", game);
		uiModel.addAttribute("question", tracker.getCurrentQuestion());

		if (tracker.hasAllPlayersAnswered()) {
			if (game.getGameType() == GameType.LEADER) {
				if (currentPlayer.equals(game.getGameLeader())) {
					Question question = Question.newQuestionForGame(game, null);
					return postQuestion1(question, uiModel);
				} else {
					// return waitForQuestionToPost(currentQuestion, uiModel);
				}
			} else if (game.getGameType() == GameType.ALLPLAYERS) {
				GameParticipant nextParticipant = tracker.getNextParticipantToPostQuestion();
				if (currentPlayer.equals(nextParticipant.getPlayer())) {
					Question question = Question.newQuestionForGame(game, nextParticipant);
					return postQuestion1(question, uiModel);
				} else {
					// return waitForQuestionToPost(currentQuestion, uiModel);
				}
			}
			return "games/waitforplayerstoanswer";
		} else {
			List<String> allParticipantIds = game.findAllParticipantIds();
			uiModel.addAttribute("allParticipantIds", SSUtil.formatListToSeparated(allParticipantIds, ","));
			List<String> answeredParticipantIds = game.findAnsweredParticipantIds(currentQuestion);
			uiModel.addAttribute("answeredParticipantIds", SSUtil.formatListToSeparated(answeredParticipantIds, ","));
			return "games/waitforplayerstoanswer";
		}
	}

	public String postQuestion1(Question question, Model uiModel) {
		uiModel.addAttribute("question", question);
		addDateTimeFormatPatterns(uiModel);
		return "games/postquestion";
	}

	@RequestMapping(value = "/postquestion2", method = RequestMethod.POST, produces = "text/html")
	public String postQuestion2(@Valid Question question, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {

		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("question", question);
			return "games/postquestion";
		}

		question.persist();

		Game game = question.getGame();
		GameTracker tracker = game.getGameTracker();
		tracker.setCurrentQuestion(question);

		if (game.getGameType().equals(GameType.LEADER))
			// Fix this
			tracker.setParticipantToAnswerFirst(null);
		else
			tracker.setParticipantToAnswerFirst(tracker.getNextParticipantToPostQuestion());

		tracker.merge();

		uiModel.asMap().clear();
		return waitForPlayersToAnswer(game.getId(), uiModel, request);
	}

	@RequestMapping(value = "answerquestion/{id}", params = "form", produces = "text/html")
	public String answerQuestionForm(@PathVariable("id") String id, Model uiModel) {
		Question question = Question.find(id);
		Player currentPlayer = UserContextService.getCurrentPlayer();
		GameParticipant participant = GameParticipant.findForGameAndPlayer(question.getGame(), currentPlayer);

		GameParticipantAnswer answer = new GameParticipantAnswer();
		answer.setGameParticipant(participant);
		answer.setQuestion(question);
		answer.setPointsBefore(participant.getPoints());

		populateAnswerQuestionForm(uiModel, answer);
		return "games/answerquestion";
	}

	@RequestMapping(value = "answerquestion", produces = "text/html")
	public String answerQuestion(@Valid GameParticipantAnswer answer, BindingResult bindingResult, Model uiModel, HttpServletRequest request) {
		if (bindingResult.hasErrors()) {
			populateAnswerQuestionForm(uiModel, answer);
			return "games/answerquestion";
		}

		answer.merge();

		uiModel.asMap().clear();
		return waitForPlayersToAnswer(answer.getQuestion().getGame().getId(), uiModel, request);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/refreshwaitingforplayers", produces = "text/html")
	public ResponseEntity<Boolean> refreshWaitingForPlayers(HttpServletRequest request) {

		boolean refresh = false;

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		String gameId = request.getParameter("gameId");
		String allParticipantIds = request.getParameter("allParticipantIds");
		String joinedParticipantIds = request.getParameter("joinedParticipantIds");

		if (SSUtil.empty(gameId) == false) {
			Game game = Game.find(gameId);
			if (game != null) {
				if (allParticipantIds != null && game.findAllParticipantIds().size() != SSUtil.formatSeparatedToArray(allParticipantIds, ",", true).length) {
					refresh = true;
				} else {
					List<String> newlyJoined;
					if (SSUtil.notEmpty(joinedParticipantIds))
						newlyJoined = game.findJoinedParticipantIdsExcept(SSUtil.formatSeparatedToArray(joinedParticipantIds, ",", true));
					else
						newlyJoined = game.findJoinedParticipantIds();
					if (SSUtil.notEmpty(newlyJoined))
						refresh = true;
				}

			}
		}

		return new ResponseEntity<Boolean>(refresh, headers, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST, value = "/refreshwaitingforaction", produces = "text/html")
	public ResponseEntity<Boolean> refreshWaitingForAction(HttpServletRequest request) {

		boolean refresh = false;

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		String gameId = request.getParameter("gameId");
		String actionToWaitFor = request.getParameter("actionToWaitFor");
		String hasFirstQuestionPosterStr = request.getParameter("hasFirstQuestionPoster");
		String hasFirstAnswererStr = request.getParameter("hasFirstAnswerer");

		boolean hasFirstQuestionPoster = false;
		boolean hasFirstAnswerer = false;

		if (hasFirstQuestionPosterStr != null)
			hasFirstQuestionPoster = new Boolean(hasFirstQuestionPosterStr);

		if (hasFirstAnswererStr != null)
			hasFirstAnswerer = new Boolean(hasFirstAnswererStr);

		Game game = Game.find(gameId);
		GameTracker tracker = game.getGameTracker();

		if (actionToWaitFor.equalsIgnoreCase(POST_QUESTION) && tracker.hasPlayerPostedQuestion() || (hasFirstQuestionPoster == false && tracker.getParticipantPostingCurrentQuestion() != null))
			refresh = true;
		else if (actionToWaitFor.equalsIgnoreCase(FIRST_ANSWER) && tracker.hasAnswered(tracker.getParticipantToAnswerFirst().getPlayer()) || (hasFirstAnswerer == false && tracker.getParticipantToAnswerFirst() != null))
			refresh = true;

		return new ResponseEntity<Boolean>(refresh, headers, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/refreshwaitingforanswers", produces = "text/html")
	public ResponseEntity<Boolean> refreshWaitingForAnswers(HttpServletRequest request) {

		boolean refresh = false;

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		String gameId = request.getParameter("gameId");
		String allParticipantIds = request.getParameter("allParticipantIds");
		String answeredParticipantIds = request.getParameter("answeredParticipantIds");

		if (SSUtil.empty(gameId) == false) {
			Game game = Game.find(gameId);
			if (game != null) {
				Question currentQuestion = game.getGameTracker().getCurrentQuestion();
				if (allParticipantIds != null && game.findAllParticipantIds().size() != SSUtil.formatSeparatedToArray(allParticipantIds, ",", true).length) {
					refresh = true;
				} else {
					List<String> newlyAnswered;
					if (SSUtil.notEmpty(answeredParticipantIds))
						newlyAnswered = game.findAnsweredParticipantIdsExcept(currentQuestion, SSUtil.formatSeparatedToArray(answeredParticipantIds, ",", true));
					else
						newlyAnswered = game.findAnsweredParticipantIds(currentQuestion);
					if (SSUtil.notEmpty(newlyAnswered))
						refresh = true;
				}

			}
		}

		return new ResponseEntity<Boolean>(refresh, headers, HttpStatus.OK);

	}

	void addDateTimeFormatPatterns(Model uiModel) {
		uiModel.addAttribute("game_created_date_format", DateTimeFormat.patternForStyle("SM", LocaleContextHolder.getLocale()));
		uiModel.addAttribute("game_started_date_format", DateTimeFormat.patternForStyle("SM", LocaleContextHolder.getLocale()));
		uiModel.addAttribute("game_finished_date_format", DateTimeFormat.patternForStyle("SM", LocaleContextHolder.getLocale()));
	}

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
		String enc = request.getCharacterEncoding();
		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}
		return UriUtils.encodePathSegment(pathSegment, enc);
	}

}
