package au.com.highlowgame.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.dbcp2.BasicDataSource;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import au.com.highlowgame.model.Game;
import au.com.highlowgame.model.GameParticipant;
import au.com.highlowgame.model.GameStatus;
import au.com.highlowgame.model.GameType;
import au.com.highlowgame.model.Player;
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

		Set<GameParticipant> selectedParticipants = new HashSet<GameParticipant>();
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

		Set<GameParticipant> selectedParticipants = new HashSet<GameParticipant>();
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

		return "redirect:/games/" + encodeUrlPathSegment(game.getId().toString(), request);
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
	public String startgame(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game == null)
			return "games/list";

		try {
			game.invitePlayers();
			game.start();
		} catch (Exception e) {
			uiModel.addAttribute("infoMessage", translationService.translate(e));
			return "games/list";
		}

		return "redirect:/games/waitforplayers/" + encodeUrlPathSegment(game.getId().toString(), request);
	}

	@RequestMapping(value = "joingame/{id}", produces = "text/html")
	public String joingame(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game != null) {
			Player currentPlayer = UserContextService.getCurrentPlayer();
			if (currentPlayer != null)
				game.playerJoined(currentPlayer);
		}

		return waitforplayers(id, uiModel, request);
	}

	@RequestMapping(value = "finishgame/{id}", produces = "text/html")
	public String finishgame(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		if (game != null)
			game.finish();

		return redirectHelper.redirectToReferer(request);
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
		uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
		uiModel.addAttribute("size", (size == null) ? "50" : size.toString());
		return "redirect:/games";
	}

	@RequestMapping(value = "waitforplayers/{id}", produces = "text/html")
	public String waitforplayers(@PathVariable("id") String id, Model uiModel, HttpServletRequest request) {
		Game game = Game.find(id);
		addDateTimeFormatPatterns(uiModel);
		uiModel.addAttribute("game", game);
		uiModel.addAttribute("itemId", id);

		return "games/waitforplayers";
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
