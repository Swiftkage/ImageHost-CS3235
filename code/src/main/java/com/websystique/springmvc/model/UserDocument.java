package com.websystique.springmvc.model;

import javax.persistence.*;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

@Entity
@Table(name="USER_DOCUMENT")
public class UserDocument {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;	
	
	@Column(name="name", length=100, nullable=false)
	private String name;
	
	@Column(name="description", length=255)
	private String description;
	
	@Column(name="type", length=100, nullable=false)
	private String type;

	@Column(name="cron", length=255)
	private String cron;

	@Column(name="info", length=100)
	private String info;

	@Lob @Basic(fetch = FetchType.LAZY)
	@Column(name="content", nullable=false)
	private byte[] content;

	@ManyToOne(optional = false)
	@JoinColumn(name = "USER_ID")
	private User user;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	public void getImage(){
//		String encodedImage = Base64.encode(getContent());
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserDocument))
			return false;
		UserDocument other = (UserDocument) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserDocument [id=" + id + ", name=" + name + ", description="
				+ description + ", type=" + type + ", cron=" + cron + "]";
	}


	
}
