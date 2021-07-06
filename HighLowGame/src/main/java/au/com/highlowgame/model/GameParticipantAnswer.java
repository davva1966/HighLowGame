package au.com.highlowgame.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import au.com.highlowgame.user.UserContextService;

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

	@DecimalMin("-999999999999999999999.9999")
	@DecimalMax("999999999999999999999.9999")
	private BigDecimal answer;

	@Enumerated(EnumType.STRING)
	private Answer highLowAnswer;

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

	public BigDecimal getAnswer() {
		return answer;
	}

	public void setAnswer(BigDecimal answer) {
		this.answer = answer;
	}

	public Answer getHighLowAnswer() {
		return highLowAnswer;
	}

	public void setHighLowAnswer(Answer highLowAnswer) {
		this.highLowAnswer = highLowAnswer;
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

	public boolean isCurrentPlayerPostingQuestion() {
		Player currentPlayer = UserContextService.getCurrentPlayer();
		return getQuestion().getGame().getGameTracker().getParticipantPostingCurrentQuestion().getPlayer().equals(currentPlayer);
	}

	public boolean isCurrentPlayerAnsweringFirst() {
		Player currentPlayer = UserContextService.getCurrentPlayer();
		return getQuestion().getGame().getGameTracker().getParticipantToAnswerFirst().getPlayer().equals(currentPlayer);
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

	public static List<GameParticipantAnswer> getForGameParticipant(GameParticipant gameParticipant) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipantAnswer> q = em.createQuery("SELECT o FROM GameParticipantAnswer AS o WHERE o.gameParticipant = :gameParticipant", GameParticipantAnswer.class);
		q.setParameter("gameParticipant", gameParticipant);
		return q.getResultList();
	}

}
