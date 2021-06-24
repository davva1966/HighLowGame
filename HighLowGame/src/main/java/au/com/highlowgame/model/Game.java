package au.com.highlowgame.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import au.com.highlowgame.service.EmailService;
import au.com.highlowgame.service.TranslationService;
import au.com.highlowgame.user.UserContextService;

@Configurable
@Entity
public class Game extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@Autowired
	transient TranslationService translationService;

	@Autowired
	transient EmailService emailService;

	@NotNull
	@ManyToOne
	private Player owner;

	@NotNull
	@Column(unique = true)
	@Size(max = 2000)
	private String description;

	@NotNull
	private int startingPoints;

	@NotNull
	@Enumerated(EnumType.STRING)
	private GameType gameType;

	@ManyToOne
	private Player gameLeader;

	private int questionsPerPlayer;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	private Date started;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	private Date finished;

	@NotNull
	@Enumerated(EnumType.STRING)
	private GameStatus gameStatus;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	private Set<GameParticipant> participants = new HashSet<GameParticipant>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	private Set<Question> questions = new HashSet<Question>();

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStartingPoints() {
		return startingPoints;
	}

	public void setStartingPoints(int startingPoints) {
		this.startingPoints = startingPoints;
	}

	public GameType getGameType() {
		if (gameType == null)
			return GameType.ALLPLAYERS;
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public Player getGameLeader() {
		return gameLeader;
	}

	public void setGameLeader(Player gameLeader) {
		this.gameLeader = gameLeader;
	}

	public int getQuestionsPerPlayer() {
		return questionsPerPlayer;
	}

	public void setQuestionsPerPlayer(int questionsPerPlayer) {
		this.questionsPerPlayer = questionsPerPlayer;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getStarted() {
		return started;
	}

	public void setStarted(Date started) {
		this.started = started;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public GameStatus getGameStatus() {
		if (gameStatus == null)
			return GameStatus.CREATED;
		return gameStatus;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public Set<GameParticipant> getParticipants() {
		if (participants == null || participants.isEmpty())
			participants = GameParticipant.getForGameByName(this);
		return participants;
	}

	public void setParticipants(Set<GameParticipant> participants) {
		this.participants = participants;
	}

	public Set<Question> getQuestions() {
		if (questions == null || questions.isEmpty())
			questions = Question.getForGame(this);
		return questions;
	}

	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	public Set<GameParticipant> getParticipantsByPoints() {
		if (participants == null || participants.isEmpty())
			participants = GameParticipant.getForGameByPoints(this);
		return participants;
	}

	public List<Player> getPlayers() {
		Set<GameParticipant> participants = getParticipants();
		if (participants == null || participants.size() == 0)
			return null;

		List<Player> players = new ArrayList<Player>();
		for (GameParticipant participant : participants) {
			players.add(participant.getPlayer());
		}

		return players;
	}

	public List<String> getPlayerIds() {
		List<Player> players = getPlayers();

		if (players == null)
			return null;

		List<String> playerIds = new ArrayList<String>();
		for (Player player : players) {
			playerIds.add(player.getId());
		}

		return playerIds;
	}

	public boolean getAllowUpdate() {
		if (gameStatus == GameStatus.CREATED) {
			Player currentPlayer = UserContextService.getCurrentPlayer();
			if (currentPlayer != null)
				return currentPlayer.isAdmin() || currentPlayer.equals(getOwner());
		}

		return false;
	}

	public boolean getAllowDelete() {
		if (gameStatus == GameStatus.CREATED || gameStatus == GameStatus.FINISHED) {
			Player currentPlayer = UserContextService.getCurrentPlayer();
			if (currentPlayer != null)
				return currentPlayer.isAdmin() || currentPlayer.equals(getOwner());
		}

		return false;
	}

	public boolean getHasLeader() {
		return getGameType() == GameType.LEADER;
	}

	public boolean getHasNoLeader() {
		return getGameType() == GameType.ALLPLAYERS;
	}

	public String getGameActionLinks(Integer page, Integer size) {
		if (getGameStatus() == GameStatus.FINISHED)
			return null;

		Player currentPlayer = UserContextService.getCurrentPlayer();
		if (currentPlayer == null)
			return null;

		if (getGameType() == GameType.LEADER)
			return getGameActionLinksForGameTypeLeader(currentPlayer, page, size);
		else if (getGameType() == GameType.ALLPLAYERS)
			return getGameActionLinksForGameTypeAll(currentPlayer, page, size);
		else
			return null;

	}

	protected String getGameActionLinksForGameTypeLeader(Player currentPlayer, Integer page, Integer size) {
		String action1 = null;
		String actionName1 = null;
		String action2 = null;
		String actionName2 = null;

		switch (getGameStatus()) {
		case CREATED:
			if (currentPlayer.equals(getGameLeader()) || currentPlayer.isAdmin()) {
				action1 = "games/startgame/" + getId();
				actionName1 = "game_action_start";
			}
			break;
		case ACTIVE:
			if (hasParticipant(currentPlayer)) {
				action1 = "games/joingame/" + getId();
				actionName1 = "game_action_join";
			}
			if (currentPlayer.isAdmin() || currentPlayer.equals(getOwner())) {
				action2 = "games/finishgame/" + getId();
				actionName2 = "game_action_finish";
			}
			break;
		default:
			break;
		}

		if (action1 != null) {
			if (page != null)
				action1 = action1 + "?page=" + page;
			if (size != null) {
				if (page == null)
					action1 = action1 + "?size=" + size;
				else
					action1 = action1 + "&size=" + size;
			}
		}

		if (action2 != null) {
			if (page != null)
				action2 = action2 + "?page=" + page;
			if (size != null) {
				if (page == null)
					action2 = action2 + "?size=" + size;
				else
					action2 = action2 + "&size=" + size;
			}
		}

		String links = "";
		if (action1 != null)
			links = translationService.translate("game_action_link", action1, translationService.translate(actionName1));
		if (action2 != null) {
			if (action1 != null)
				links = links + "&nbsp&nbsp&nbsp";
			links = links + translationService.translate("game_action_link", action2, translationService.translate(actionName2));
		}

		return links;

	}

	protected String getGameActionLinksForGameTypeAll(Player currentPlayer, Integer page, Integer size) {
		String action1 = null;
		String actionName1 = null;
		String action2 = null;
		String actionName2 = null;

		switch (getGameStatus()) {
		case CREATED:
			if (currentPlayer.equals(getOwner()) || currentPlayer.isAdmin()) {
				action1 = "games/startgame/" + getId();
				actionName1 = "game_action_start";
			}
			break;
		case ACTIVE:
			if (hasParticipant(currentPlayer)) {
				action1 = "games/joingame/" + getId();
				actionName1 = "game_action_join";
			}
			if (currentPlayer.isAdmin() || currentPlayer.equals(getOwner())) {
				action2 = "games/finishgame/" + getId();
				actionName2 = "game_action_finish";
			}
			break;
		default:
			break;
		}

		if (action1 != null) {
			if (page != null)
				action1 = action1 + "?page=" + page;
			if (size != null) {
				if (page == null)
					action1 = action1 + "?size=" + size;
				else
					action1 = action1 + "&size=" + size;
			}
		}

		if (action2 != null) {
			if (page != null)
				action2 = action2 + "?page=" + page;
			if (size != null) {
				if (page == null)
					action2 = action2 + "?size=" + size;
				else
					action2 = action2 + "&size=" + size;
			}
		}

		String links = "";
		if (action1 != null)
			links = translationService.translate("game_action_link", action1, translationService.translate(actionName1));
		if (action2 != null) {
			if (action1 != null)
				links = links + "&nbsp&nbsp&nbsp";
			links = links + translationService.translate("game_action_link", action2, translationService.translate(actionName2));
		}

		return links;

	}

	@PrePersist
	protected void prePersist() {
		if (getOwner() == null)
			setOwner(UserContextService.getCurrentPlayer());
		if (getGameType() == GameType.ALLPLAYERS)
			setGameLeader(null);
		if (getGameType() == GameType.LEADER && getGameLeader() == null)
			setGameLeader(getOwner());
		if (getCreated() == null)
			setCreated(new Date());
		if (getGameStatus() == null)
			setGameStatus(GameStatus.CREATED);
	}

	@Transactional
	public Game merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		Game merged = this.entityManager.merge(this);
		return merged;
	}

	public void removeAllParticiants() {

		if (getParticipants() == null)
			return;

		for (GameParticipant participant : getParticipants()) {
			participant.remove();
		}
		setParticipants(null);
	}

	public void start() {
		setStarted(new Date());
		setGameStatus(GameStatus.ACTIVE);
		merge();
	}

	public synchronized void playerJoined(Player player) {
		GameParticipant participant = findParticipantForPlayer(player);
		if (participant != null) {
			participant.setNumber(GameParticipant.getNextNumberForGame(this));
			participant.merge();
		}
	}

	public void finish() {
		setFinished(new Date());
		setGameStatus(GameStatus.FINISHED);
		merge();
	}

	public void invitePlayers() throws Exception {

		if (getParticipants() == null)
			return;

		for (GameParticipant participant : getParticipants()) {
			emailService.sendGameInvitation(this, participant);
		}

	}

	public String getJoinLinkFor(GameParticipant participant) throws Exception {
		String link = "http://localhost:6088/HighLowGame/";
		link = link + "games/joingame/?game=" + getId() + "&participant=" + participant.getId();

		return link;
	}

	public String getDeclineLinkFor(GameParticipant participant) throws Exception {
		String link = "http://localhost:6088/HighLowGame/";
		link = link + "games/declinegame/?game=" + getId() + "&participant=" + participant.getId();

		return link;
	}

	public boolean hasParticipant(Player player) {
		return findParticipantForPlayer(player) != null;
	}

	public GameParticipant findParticipantForPlayer(Player player) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipant> q = em.createQuery("SELECT o FROM GameParticipant o WHERE o.game = :game and o.player = :player", GameParticipant.class);
		q.setParameter("game", this);
		q.setParameter("player", player);

		try {
			return q.getSingleResult();
		} catch (Exception e) {
		}

		return null;
	}

	public static Game find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(Game.class, id);
	}

	public static List<Game> findAllGames() {

		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Game> q = null;

		Player currentPlayer = UserContextService.getCurrentPlayer();
		if (currentPlayer != null && currentPlayer.isAdmin()) {
			q = em.createQuery("SELECT o FROM Game o order by o.created desc", Game.class);
		} else {
			q = em.createQuery("SELECT o FROM Game o WHERE o.owner = :owner order by o.created desc", Game.class);
			q.setParameter("owner", currentPlayer);
		}

		return q.getResultList();
	}

	public static List<Game> findGameEntries(int firstResult, int maxResults) {

		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Game> q = null;

		Player currentPlayer = UserContextService.getCurrentPlayer();
		if (currentPlayer == null)
			return null;

		if (currentPlayer.isAdmin()) {
			q = em.createQuery("SELECT o FROM Game o order by o.created desc", Game.class).setFirstResult(firstResult).setMaxResults(maxResults);
		} else {
			q = em.createQuery("SELECT DISTINCT(o.game) FROM GameParticipant o WHERE o.player = :player order by o.game.created desc", Game.class).setFirstResult(firstResult).setMaxResults(maxResults);
			q.setParameter("player", currentPlayer);
		}

		return q.getResultList();

	}

	public static long countGames() {

		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Long> q = null;

		Player currentPlayer = UserContextService.getCurrentPlayer();
		if (currentPlayer == null)
			return 0;

		if (currentPlayer.isAdmin()) {
			q = em.createQuery("SELECT COUNT(o) FROM Game o", Long.class);
		} else {
			q = em.createQuery("SELECT COUNT(DISTINCT o.game) FROM GameParticipant o WHERE o.player = :player", Long.class);
			q.setParameter("player", currentPlayer);
		}

		return q.getSingleResult();
	}

}
