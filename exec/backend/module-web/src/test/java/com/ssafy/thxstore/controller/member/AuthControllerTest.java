package com.ssafy.thxstore.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.thxstore.controller.common.BaseControllerTest;
import com.ssafy.thxstore.controller.member.docs.AuthDocumentation;
import com.ssafy.thxstore.member.domain.Social;
import com.ssafy.thxstore.member.dto.request.SendEmailRequest;
import com.ssafy.thxstore.member.dto.request.SignUpRequest;
import com.ssafy.thxstore.member.dto.request.SocialMemberRequest;
import com.ssafy.thxstore.member.dto.response.CheckEmailResponse;
import com.ssafy.thxstore.member.repository.MemberRepository;
import com.ssafy.thxstore.member.service.AuthService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseControllerTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AuthService authService;



    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentationContextProvider) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .build();

        this.memberRepository.deleteAll();
    }

    @Test
    @DisplayName("일반회원 회원가입 진행")
    public void registerLocalMember() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("test456@gmail.com")
                .password("Pasword123!")
                .nickname("sangwoo")
                .lat(37.33)
                .lon(126.59)
                .address("서울 중구 광화문")
                .build();

        mockMvc.perform(post("/auth/")
                .content(new ObjectMapper().writeValueAsString(signUpRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(AuthDocumentation.signUpLOCAL());
    }

    @Test
    @DisplayName("소셜 회원가입 진행")
    public void registerSocialMember() throws Exception {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("test123@gmail.com")
                .password("Pasword123!")
                .nickname("sangwoo")
                .social(Social.KAKAO)
                .userId("providerID123")
                .profileImage("Default Profile link")
                .lat(37.33)
                .lon(126.59)
                .address("서울 중구 광화문")
                .build();

        mockMvc.perform(post("/auth/")
                .content(new ObjectMapper().writeValueAsString(signUpRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(AuthDocumentation.signUpSOCIAL());
    }

    @Test
    @DisplayName("소셜회원에 대한 정보값을 가져온다.")
    public void getSocialMember() throws Exception{
        //Given
        this.generateMember("socialMemberEmail@email.com","social");
        SocialMemberRequest socialMemberRequest = SocialMemberRequest.builder()
                .social(Social.KAKAO)
                .userId("kakao userId"+1)
                .build();
        //When & Then
        mockMvc.perform(post("/auth/social/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(socialMemberRequest)))
                .andExpect(result -> CheckEmailResponse.of(true))
                .andDo(print())
                .andDo(AuthDocumentation.getSocialMember());
    }
    @Test
    @DisplayName("존재하지 않는 소셜회원에 대한 정보값을 가져온다.")
    public void getNotExistSocialMember() throws Exception{
        //Given
        this.generateMember("notExistSocialMemberMemberEmail@email.com","notSocial");
        SocialMemberRequest socialMemberRequest = SocialMemberRequest.builder()
                .social(Social.KAKAO)
                .userId("not exist userId"+1)
                .build();
        //When & Then
        mockMvc.perform(post("/auth/social/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(socialMemberRequest)))
                .andExpect(result -> CheckEmailResponse.of(false))
                .andDo(print())
                .andDo(AuthDocumentation.getSocialMember());
    }

    @Test
    @DisplayName("이메일 중복이 일어나지 않을때.")
    public void checkSignUpMemberEmail() throws Exception{
        //Given
        String email = "test@gmail.com";
        generateMember("testExistCheckEmail@email.com","testExistCheckEmail");
        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>();
        requestParam.set("email",email);
        //When & Then
        mockMvc.perform(get("/auth/checkEmail/").params(requestParam)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(requestParam)))
                .andExpect(result -> CheckEmailResponse.of(false))
                .andDo(print())
                .andDo(AuthDocumentation.getCheckEmail());
    }

    @Test
    @DisplayName("이메일 중복이 일어날때.")
    public void failCheckSignUpMemberEmail() throws Exception{
        //Given
        String email = "testCheckEmail@gmail.com";
        generateMember("testCheckEmail@email.com","testCheck");
        MultiValueMap<String, String> requestParam = new LinkedMultiValueMap<>();
        requestParam.set("email",email);
        //When & Then
        mockMvc.perform(get("/auth/checkEmail/").params(requestParam)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestParam)))
                .andExpect(result -> CheckEmailResponse.of(true))
                .andDo(print())
                .andDo(AuthDocumentation.getCheckEmail());
    }

    @Test
    @DisplayName("해당 이메일로 인증메일 보내기")
    public void sendEmail() throws Exception{
        //Given
        String email = "helloEmail@gmail.com";
        generateMember(email,"testEmailCheck");
        //Given
        SendEmailRequest sendEmailRequest= SendEmailRequest.builder().email(email).build();

        //When & Then
        mockMvc.perform(post("/auth/email/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(sendEmailRequest)))
                .andExpect(result -> CheckEmailResponse.of(true))
                .andDo(print())
                .andDo(AuthDocumentation.sendEmail());
    }
}



