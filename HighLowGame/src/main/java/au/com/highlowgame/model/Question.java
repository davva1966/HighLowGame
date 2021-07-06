package au.com.highlowgame.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class Question extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	private Game game;

	@ManyToOne
	private GameParticipant owner;

	@NotNull
	private int number;

	@Size(max = 2048)
	private String question;

	@NotNull
	@DecimalMin("-999999999999999999999.9999")
	@DecimalMax("999999999999999999999.9999")
	private BigDecimal correctAnswer;

	@Size(max = 100)
	private String unit;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public GameParticipant getOwner() {
		return owner;
	}

	public void setOwner(GameParticipant owner) {
		this.owner = owner;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public BigDecimal getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(BigDecimal correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Transactional
	public Question merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		Question merged = this.entityManager.merge(this);
		return merged;
	}

	public static Question newQuestionForGame(Game game, GameParticipant owner) {
		Question question = new Question();
		question.setGame(game);
		if (owner != null)
			question.setOwner(owner);
		question.setNumber(Question.getNextNumberForGame(game));
		return question;
	}

	public static Question find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(Question.class, id);
	}

	public static List<Question> getForGame(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Question> q = em.createQuery("SELECT o FROM Question AS o WHERE o.game = :game order by o.number", Question.class);
		q.setParameter("game", game);
		return q.getResultList();
	}

	public static int getNextNumberForGame(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Integer> q = em.createQuery("SELECT o.number FROM Question AS o WHERE o.game = :game order by o.number desc", Integer.class).setMaxResults(1);
		q.setParameter("game", game);

		int number = 0;
		try {
			number = q.getSingleResult();
		} catch (Exception e) {
		}

		return number + 1;

	}

}
