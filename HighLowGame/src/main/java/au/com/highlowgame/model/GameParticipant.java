package au.com.highlowgame.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import au.com.highlowgame.util.Application;

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

	@NotNull
	private int number;

	private int points;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "gameParticipant")
	private List<GameParticipantAnswer> answers = new ArrayList<GameParticipantAnswer>();

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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public List<GameParticipantAnswer> getAnswers() {
		if (answers == null || answers.isEmpty())
			answers = GameParticipantAnswer.getForGameParticipant(this);
		return answers;
	}

	public void setAnswers(List<GameParticipantAnswer> answers) {
		this.answers = answers;
	}

	public String getName() {
		return getPlayer().getName();
	}

	public String getAvatarThumbnailUrl() {
		return getPlayer().getAvatarThumbnailUrl();
	}

	public String getJoinedOrWaiting() {
		if (getNumber() == 0)
			return "<img src=\"" + Application.getRelativeURL() + "resources/images/waiting.gif\">";
		else
			return "<img src=\"" + Application.getRelativeURL() + "resources/images/checkmark.png\">";
	}

	public String getAnsweredOrWaiting() {
		if (GameParticipantAnswer.getForGameParticipant(this) == null)
			return "<img src=\"" + Application.getRelativeURL() + "resources/images/waiting.gif\">";
		else
			return "<img src=\"" + Application.getRelativeURL() + "resources/images/checkmark.png\">";
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

	public static List<GameParticipant> getForGameByName(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipant> q = em.createQuery("SELECT o FROM GameParticipant AS o WHERE o.game = :game order by o.player.name", GameParticipant.class);
		q.setParameter("game", game);
		return q.getResultList();
	}

	public static GameParticipant findForGameAndPlayer(Game game, Player player) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipant> q = em.createQuery("SELECT o FROM GameParticipant AS o WHERE o.game = :game and o.player = :player", GameParticipant.class);
		q.setParameter("game", game);
		q.setParameter("player", player);
		try {
			return q.getSingleResult();
		} catch (Exception e) {
		}
		return null;
	}

	public static List<GameParticipant> getForGameByPoints(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<GameParticipant> q = em.createQuery("SELECT o FROM GameParticipant AS o WHERE o.game = :game order by o.points desc", GameParticipant.class);
		q.setParameter("game", game);
		return q.getResultList();
	}

	public static int getNextNumberForGame(Game game) {
		EntityManager em = DomainEntity.entityManager();
		TypedQuery<Integer> q = em.createQuery("SELECT o.number FROM GameParticipant AS o WHERE o.game = :game order by o.number desc", Integer.class).setMaxResults(1);
		q.setParameter("game", game);

		int number = 0;
		try {
			number = q.getSingleResult();
		} catch (Exception e) {
		}

		return number + 1;

	}

}
