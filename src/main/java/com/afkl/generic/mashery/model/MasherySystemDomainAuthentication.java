package com.afkl.generic.mashery.model;

import com.fasterxml.jackson.annotation.JsonValue;



public class MasherySystemDomainAuthentication {

	private DomainAuthType type;

	private String username;

	private String certificate;

	private String password;

	public DomainAuthType getType() {
		return type;
	}

	public void setType(DomainAuthType type) {
		this.type = type;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public enum DomainAuthType {
		HTTP_BASIC("httpBasic"),
		CLIENT_SSL_CERT("clientSslCert");

		private final String string;

		private DomainAuthType(String authType) {
			this.string = authType;
		}

		@Override
		@JsonValue
		public String toString() {
			return string;
		}

		public static DomainAuthType fromString(String authType) {
			for (DomainAuthType val : DomainAuthType.values()) {
				if (val.string.equalsIgnoreCase(authType))
					return val;
			}
			return null;
		}
	}
}
