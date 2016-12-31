package com.snslogin.domain;

import lombok.Data;

public class Kakao {

	@Data
	public static class Token {
		private String access_token;
		private String token_type;
		private String refresh_token;
		private String expires_in;
		private String scope;
	}

	@Data
	public static class Account {

		private String id;
		private Properties properties;

		@Data
		public static class Properties {
			private String nickname;
			private String profile_image;
			private String thumbnail_image;
			private String status;
			private String created;
		}
	}

}
