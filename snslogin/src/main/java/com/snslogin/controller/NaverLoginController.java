package com.snslogin.controller;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.snslogin.domain.Naver;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class NaverLoginController {

	// 애플리케이션 등록 후 발급받은 클라이언트 아이디 [수정 O ]
	private final String clientId = "9NEoJO6uGCvKGA8S62V2";
	// 애플리케이션이 생성한 상태 토큰 [수정 X ]
	private final String CurrentState = stateToken();
	// 클라이언트 시크릿 [수정 O ]
	private final String clientSecret = "0451rG9YjH";
	// 네이버 로그인 인증의 결과를 전달받을 콜백 URL(URL 인코딩) [수정 X ]
	private String redirectURL = "";
	// [수정 O ]
	private final String RedirectURI = "/redirect_uri_naver";

	// 사용자 토큰 요청할 URL [수정 X ]
	private final String tokenURL = "https://nid.naver.com/oauth2.0/token?"
			+ "client_id={clientId}&client_secret={clientSecret}&grant_type=authorization_code&"
			+ "state={state}&code={certificationCode}";

	// 요청할때 사용할 객체.
	private RestTemplate restTemplate = new RestTemplate();

	// 로그인 요청 성공후 콜백 되는 URL
	@GetMapping("redirect_uri_naver")
	// code : 로그인 요청 성공후 얻은 값
	// state : 로그인 요청이 들어왔을때 생성되는 stateToken()메서드의 값
	public String login2(final String code, final String state) {

		Naver.Token naver = restTemplate.getForObject(tokenURL, Naver.Token.class, clientId, clientSecret, state, code);
		log.info("[NaverLogin] [토큰 접근] [얻어온 정보 : {}]", naver);

		HttpHeaders headers = new HttpHeaders();
		// Authorization 헤더 값추가해서
		headers.add("Authorization", "Bearer " + naver.getAccess_token());
		final String findByUserUrl = "https://openapi.naver.com/v1/nid/me";
		// 발급 받은 사용자 토큰을 사용해서 네이버에 정보 요청
		ResponseEntity<Naver> naverEntity = restTemplate.exchange(findByUserUrl, HttpMethod.GET,
				new HttpEntity<>(headers), Naver.class);
		log.info("[NaverLogin] [로그인한 사용자 정보 : {}]", naverEntity.getBody().getResponse());

		return "redirect:/";
	}

	// 로그인 요청
	@GetMapping("naverlogin")
	public String login(HttpServletRequest request) {
		// 인증의 결과를 전달받을 콜백 URL 얻어 온다. ( 응답 데이터 : http://Host/redirecturikakao )
		redirectURL = request.getRequestURL().toString().replace(request.getServletPath(), RedirectURI);
		
		log.info("[NaverLogin] [로그인을 요청한 클라이언트 정보] [url : {}], [ip : {}]", redirectURL, request.getRemoteAddr());

		return String.format(
				"redirect:https://nid.naver.com/oauth2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&state=%s",
				clientId, redirectURL, CurrentState);
	}

	// 상태 토큰으로 사용할 랜덤 문자열 생성
	public String stateToken() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

}