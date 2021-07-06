package au.com.highlowgame.model;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class GameTracker extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	private Game game;

	@ManyToOne
	private Question currentQuestion;

	@ManyToOne
	private GameParticipant participantPostingCurrentQuestion;

	@ManyToOne
	private GameParticipant participantToAnswerFirst;

	public GameTracker() {
		super();
	}

	public GameTracker(@NotNull Game game) {
		super();
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Question getCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public GameParticipant getParticipantPostingCurrentQuestion() {
		return participantPostingCurrentQuestion;
	}

	public void setParticipantPostingCurrentQuestion(GameParticipant participantPostingCurrentQuestion) {
		this.participantPostingCurrentQuestion = participantPostingCurrentQuestion;
	}

	public GameParticipant getParticipantToAnswerFirst() {
		return participantToAnswerFirst;
	}

	public void setParticipantToAnswerFirst(GameParticipant participantToAnswerFirst) {
		this.participantToAnswerFirst = participantToAnswerFirst;
	}

	@Transactional
	public GameTracker merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		GameTracker merged = this.entityManager.merge(this);
		return merged;
	}

	public boolean hasPlayerPostedQuestion() {
		return getCurrentQuestion() != null && getParticipantPostingCurrentQuestion().equals(getCurrentQuestion().getOwner());
	}

	public boolean hasAnswered(Player player) {
		GameParticipant gameParticipant = GameParticipant.findForGameAndPlayer(getGame(), player);
		if (gameParticipant == null)
			return false;

		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipantAnswer> q = em.createQuery("SELECT o FROM GameParticipantAnswer AS o WHERE o.gameParticipant = :gameParticipant and o.question = :question", GameParticipantAnswer.class);
		q.setParameter("gameParticipant", gameParticipant);
		q.setParameter("question", getCurrentQuestion());
		return q.getResultList() != null && q.getResultList().size() > 0;
	}

	public boolean hasAllPlayersAnswered() {
		int numberOfPlayers = getGame().getParticipants().size();

		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Long> q = em.createQuery("SELECT COUNT(o) FROM GameParticipantAnswer AS o WHERE o.question = :question", Long.class);
		q.setParameter("question", getCurrentQuestion());
		return q.getSingleResult() >= numberOfPlayers;
	}

	public GameParticipant getNextParticipantToPostQuestion() {
		int numberOfPlayers = getGame().getParticipants().size();
		int currentNumber = getParticipantPostingCurrentQuestion().getNumber();
		int nextNumber = currentNumber + 1;
		if (currentNumber >= numberOfPlayers)
			nextNumber = 1;

		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipant> q = em.createQuery("SELECT o FROM GameParticipant AS o WHERE o.game = :game and o.number = :number", GameParticipant.class);
		q.setParameter("game", getGame());
		q.setParameter("number", nextNumber);
		return q.getSingleResult();
	}
	
	public static GameTracker find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(GameTracker.class, id);
	}

	public static GameTracker getForGame(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameTracker> q = em.createQuery("SELECT o FROM GameTracker AS o WHERE o.game = :game", GameTracker.class);
		q.setParameter("game", game);
		return q.getSingleResult();
	}

}
