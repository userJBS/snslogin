package com.snslogin.domain;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "data")
@Data
public class Naver {

	private NResult result;
	private NResponse response;

	@Data
	public static class NResult {
		private String resultcode;
		private String message;
	}

	@Data
	public static class NResponse {
		private String enc_id;
		private String nickname;
		private String id;
		private String gender;
		private String age;
		private String birthday;
		private String profile_image;
	}

}
