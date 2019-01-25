package com.devopsbuddy.backend.persistence.domain.backend;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

import org.h2.command.ddl.CreateAggregate;

import com.devopsbuddy.backend.persistence.converter.LocalDateTimeAttributeConverter;
import com.devopsbuddy.web.controllers.ContactController;

@Entity
public class PasswordResetToken implements Serializable {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PasswordResetToken.class);

	private static final int DEFAULT_TOKEN_LENGTH_MINUTES = 120;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(unique = true)
	private String token;

	@ManyToOne(fetch = FetchType.EAGER)
	// se pone aqui ya que será llave foranea de la entidad User
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "expiry_date")
	@Convert(converter = LocalDateTimeAttributeConverter.class)
	private LocalDateTime expiryDate;

	public PasswordResetToken() {
		super();
	}

	public PasswordResetToken(String token, User user, LocalDateTime creationDateTime, int expirationMinutes) {
		if ((null == token) || (null == user) || (null == creationDateTime)) {
			throw new IllegalArgumentException("token, user and creation date time can't be null");
		}
		if (expirationMinutes == 0) {
			LOG.warn("The token expiration length in minutes  is zero. Assigning the default value {}",
					DEFAULT_TOKEN_LENGTH_MINUTES);
			expirationMinutes = DEFAULT_TOKEN_LENGTH_MINUTES;
		}
		this.token = token;
		this.user = user;
		expiryDate = creationDateTime.plusMinutes(expirationMinutes);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PasswordResetToken other = (PasswordResetToken) obj;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (id != other.id)
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PasswordResetToken [id=");
		builder.append(id);
		builder.append(", token=");
		builder.append(token);
		builder.append(", user=");
		builder.append(user);
		builder.append(", expiryDate=");
		builder.append(expiryDate);
		builder.append("]");
		return builder.toString();
	}

}
