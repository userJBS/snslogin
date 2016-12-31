package com.snslogin.controller;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NaverLoginController {

	// 애플리케이션 등록 후 발급받은 클라이언트 아이디
	private final String clientId = "9NEoJO6uGCvKGA8S62V2";
	// 네이버 로그인 인증의 결과를 전달받을 콜백 URL(URL 인코딩).
	private final String redirectUri = "http://localhost:7070/redirectUri";
	// 애플리케이션이 생성한 상태 토큰
	private final String CurrentState = generateState();
	// 클라이언트 시크릿
	private final String clientSecret = "0451rG9YjH";
	// 로그인 했을때 받는 인증 코드
	private String certificationCode = "";

	@GetMapping(value = "/redirectUri")
	public String redirectUri(String code, String state) {
		// certificationCode = code;
		System.out.println("!redirectUri : " + code);
		// System.out.println("!redirectUri : " + state);
		// System.out.println("!redirectUri : " + state);

		// CSRF 방지를 위한 상태 토큰 검증
		// 세션 또는 별도의 저장 공간에 저장된 상태 토큰과 콜백으로 전달받은 state 파라미터의 값이 일치해야 함

		// 콜백 응답에 state 파라미터의 값 과 저장 공간에 state값 비교
		// if (!state.equals(CurrentState)) {
		// return RESPONSE_UNAUTHORIZED; // 401 unauthorized
		// } else {
		// Return RESPONSE_SUCCESS; // 200 success
		// }

		return "index";
	}

	// 2차 접근 토큰 발급 요청
	@GetMapping(value = "/login2")
	public String login2() {
		System.out.println("@login2@ : " + CurrentState);
		return String.format(
				"redirect:https://nid.naver.com/oauth2.0/token?client_id=%s&client_secret=%s&grant_type=authorization_code&state=%s&code=%s",
				clientId, clientSecret, CurrentState, certificationCode);
	}

	// 1차 로그인 요청
	@GetMapping(value = "/login")
	public String login() {
		System.out.println("#login : " + CurrentState);
		return String.format(
				"redirect:https://nid.naver.com/oauth2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s",
				clientId, redirectUri, CurrentState);
	}

	// 상태 토큰으로 사용할 랜덤 문자열 생성
	public String generateState() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

}