package au.com.highlowgame.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Configurable
public class GameParticipant extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@ManyToOne
	private Game game;

	@NotNull
	@ManyToOne
	private Player player;

	private int points;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "gameParticipant")
	private Set<GameParticipantAnswer> answers = new HashSet<GameParticipantAnswer>();

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	public Set<GameParticipantAnswer> getAnswers() {
		if (answers == null || answers.isEmpty())
			answers = GameParticipantAnswer.getForGameParticipant(this);
		return answers;
	}

	public void setAnswers(Set<GameParticipantAnswer> answers) {
		this.answers = answers;
	}

	@Transactional
	public GameParticipant merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		GameParticipant merged = this.entityManager.merge(this);
		return merged;
	}
	
	public static GameParticipant find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(GameParticipant.class, id);
	}
	
	public static Set<GameParticipant> getForGame(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipant> q = em.createQuery("SELECT o FROM GameParticipant AS o WHERE o.game = :game", GameParticipant.class);
		q.setParameter("game", game);
		return new HashSet<GameParticipant>(q.getResultList());
	}

}
