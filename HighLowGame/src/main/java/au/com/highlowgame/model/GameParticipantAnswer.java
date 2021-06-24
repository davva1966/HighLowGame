package au.com.highlowgame.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class GameParticipantAnswer extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	private GameParticipant gameParticipant;

	@NotNull
	@OneToOne
	private Question question;

	@Size(max = 300)
	private String answer;

	private Boolean correct;

	private int pointsBefore;

	private int pointsBet;

	private int pointsAfter;

	public GameParticipant getGameParticipant() {
		return gameParticipant;
	}

	public void setGameParticipant(GameParticipant gameParticipant) {
		this.gameParticipant = gameParticipant;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Boolean getCorrect() {
		return correct;
	}

	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}

	public int getPointsBefore() {
		return pointsBefore;
	}

	public void setPointsBefore(int pointsBefore) {
		this.pointsBefore = pointsBefore;
	}

	public int getPointsBet() {
		return pointsBet;
	}

	public void setPointsBet(int pointsBet) {
		this.pointsBet = pointsBet;
	}

	public int getPointsAfter() {
		return pointsAfter;
	}

	public void setPointsAfter(int pointsAfter) {
		this.pointsAfter = pointsAfter;
	}

	@Transactional
	public GameParticipantAnswer merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		GameParticipantAnswer merged = this.entityManager.merge(this);
		return merged;
	}

	public static GameParticipantAnswer find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(GameParticipantAnswer.class, id);
	}

	public static Set<GameParticipantAnswer> getForGameParticipant(GameParticipant gameParticipant) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipantAnswer> q = em.createQuery("SELECT o FROM GameParticipantAnswer AS o WHERE o.gameParticipant = :gameParticipant", GameParticipantAnswer.class);
		q.setParameter("gameParticipant", gameParticipant);
		return new HashSet<GameParticipantAnswer>(q.getResultList());
	}

}
