package au.com.highlowgame.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
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

	@NotNull
	private int number;

	@Size(max = 2048)
	private String question;

	@NotNull
	@Size(max = 300)
	private String correctAnswer;

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
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

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	@Transactional
	public Question merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		Question merged = this.entityManager.merge(this);
		return merged;
	}

	public static Question find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(Question.class, id);
	}

	public static Set<Question> getForGame(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Question> q = em.createQuery("SELECT o FROM Question AS o WHERE o.game = :game order by o.number", Question.class);
		q.setParameter("game", game);
		return new HashSet<Question>(q.getResultList());
	}

}
