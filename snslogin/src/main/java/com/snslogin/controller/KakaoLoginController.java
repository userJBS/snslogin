package com.snslogin.controller;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.snslogin.domain.Kakao;

import lombok.extern.slf4j.Slf4j;

// 카카오 로그인
// 1. 로그인 요청 ( 요청 : /oauth/authorize?... )  
//  - 로그인 성공할 경우 code 값을 받아온다. ( 응답 : code={authorize_code} ) 
// 2. 사용자 토큰 받기 ( 요청 : /oauth/token )
//  - 응답으로 KakaoToken 객체(getter/setter로 JSON값 사용)에 데이터(JSON) 담는다.
// 3. 로그인한 사용자 정보 조회 ( 2. 사용자 토큰을 사용해서 정보를 조회)
//  - 응답...
@Controller
@Slf4j
public class KakaoLoginController {

	// 애플리케이션 등록 후 발급받은 appKey [ 수정 O (카카오 홈페이지에 설정한 콜백 주소) ]
	private final String appKey = "424b55f5ddb82448866d67cfe0a6aeec";
	// 인증의 결과를 전달받을 콜백 URL ( 로그인 요청을 처리할때 콜백 URI 값과 합쳐진다) [수정 X ]
	private String redirectURL = "";
	// 인증의 결과를 전달받을 콜백 URI . [ 수정 O (카카오 홈페이지에 설정한 콜백 주소) ]
	private final String RedirectURI = "/redirecturikakao";
	// 로그인 요청에서 얻은 code(토큰)값 [수정 X ]
	private String authorizeCode = "";
	// 사용자 토큰 요청할 URL [수정 X ]
	private final String tokenUrl = "https://kauth.kakao.com/oauth/token?"
			+ "grant_type=authorization_code&client_id={appKey}&redirect_uri={redirectUri}&code={authorizeCode}";
	// 요청할때 사용할 객체.
	private RestTemplate restTemplate = new RestTemplate();

	// 로인요청 성공후 콜백 되는 URL
	@GetMapping("/redirecturikakao")
	public String redirectUri(String code, HttpServletResponse response) {
		// 로그인 성공후 받은 code 값
		authorizeCode = code;

		// 사용자 토큰 요청
		Kakao.Token kakaoToken = restTemplate.getForObject(tokenUrl, Kakao.Token.class, appKey, redirectURL,
				authorizeCode);

		// 발급 받은 사용자 토큰을 사용해서 카카오 정보 요청
		HttpHeaders headers = new HttpHeaders();
		// Authorization 헤더 값추가해서
		headers.add("Authorization", "Bearer " + kakaoToken.getAccess_token());
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		final String findByUserUrl = "https://kapi.kakao.com/v1/user/me";
		ResponseEntity<Kakao.Account> responseEntity = restTemplate.exchange(findByUserUrl, HttpMethod.GET,
				new HttpEntity<>(headers), Kakao.Account.class);

		log.info("[Kakaologin] [로그인한 사용자 정보] [{}]", responseEntity.getBody());

		return "redirect:/";
	}

	// 로그인 요청
	@GetMapping("/kakaologin")
	public String login(HttpServletRequest request) {
		// 인증의 결과를 전달받을 콜백 URL 얻어 온다. ( 응답 데이터 : http://Host/redirecturikakao )
		redirectURL = request.getRequestURL().toString().replace(request.getServletPath(), RedirectURI);

		log.info("[Kakaologin] [로그인을 요청한 클라이언트 정보] [url : {}], [ip : {}]", redirectURL, request.getRemoteAddr());

		return String.format(
				"redirect:https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code",
				appKey, redirectURL);
	}

}