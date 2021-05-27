package au.com.highlowgame.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class Game extends DomainEntity {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(unique = true)
	@Size(max = 300)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	private Date started;

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "MM")
	private Date finished;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	private Set<GameParticipant> participants = new HashSet<GameParticipant>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Set<GameParticipant> getParticipants() {
		if (participants == null || participants.isEmpty())
			participants = GameParticipant.getForGame(this);
		return participants;
	}

	public void setParticipants(Set<GameParticipant> participants) {
		this.participants = participants;
	}

	@Transactional
	public Game merge() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		Game merged = this.entityManager.merge(this);
		return merged;
	}

	public static Game find(String id) {
		if (id == null || id.length() == 0)
			return null;
		return entityManager().find(Game.class, id);
	}

}
