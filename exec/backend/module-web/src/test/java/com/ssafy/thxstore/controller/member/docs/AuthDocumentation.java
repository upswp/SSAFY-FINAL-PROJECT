package com.ssafy.thxstore.controller.member.docs;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultHandler;

import static com.ssafy.thxstore.controller.utils.ApiDocumentsUtils.getDocumentRequest;
import static com.ssafy.thxstore.controller.utils.ApiDocumentsUtils.getDocumentResponse;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AuthDocumentation {
    public static RestDocumentationResultHandler login() {
        return document("login-member",
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("로그인할 email"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("로그인할 password")
                ),
                responseFields(
                        fieldWithPath("tokenType").type(JsonFieldType.STRING).description("Token의 타입"),
                        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("로그인으로 얻은 Token")
                )
        );
    }

    public static RestDocumentationResultHandler signUpLOCAL() {
        return document("signUp-LOCAL",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("signUp-LOCAL").description("link to query members"),
                        linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원가입할 E-mail"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("회원가입할 패스워드"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원가입할 닉네임"),
                        fieldWithPath("lat").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("lon").type(JsonFieldType.NUMBER).description("경도"),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
                ).and(subsectionWithPath("profileImage").type(JsonFieldType.NULL).description("회원 프로필 이미지"),
                        subsectionWithPath("social").type(JsonFieldType.NULL).description("회원 소셜 종류 판단"),
                        subsectionWithPath("userId").type(JsonFieldType.NULL).description("소셜 유저 아이디")),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("hal+json type")
                ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("PK"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                        fieldWithPath("lat").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("lon").type(JsonFieldType.NUMBER).description("경도"),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.signUp-LOCAL.href").description("link to query event list")
                )
        );
    }

    public static ResultHandler signUpSOCIAL() {
        return document("signUp-SOCIAL",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("signUp-SOCIAL").description("link to query members"),
                        linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("회원가입할 E-mail"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("회원가입할 패스워드"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원가입할 닉네임"),
                        fieldWithPath("profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                        fieldWithPath("social").type(JsonFieldType.STRING).description("회원 소셜 종류 판단"),
                        fieldWithPath("userId").type(JsonFieldType.STRING).description("소셜 유저 아이디"),
                        fieldWithPath("lat").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("lon").type(JsonFieldType.NUMBER).description("경도"),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("hal+json type")
                ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("PK"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("유저 이메일"),
                        fieldWithPath("password").type(JsonFieldType.STRING).description("유저 비밀번호"),
                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("유저 닉네임"),
                        fieldWithPath("profileImage").type(JsonFieldType.STRING).description("회원 프로필 이미지"),
                        fieldWithPath("social").type(JsonFieldType.STRING).description("회원 소셜 종류 판단"),
                        fieldWithPath("userId").type(JsonFieldType.STRING).description("소셜 유저 아이디"),
                        fieldWithPath("lat").type(JsonFieldType.NUMBER).description("위도"),
                        fieldWithPath("lon").type(JsonFieldType.NUMBER).description("경도"),
                        fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.signUp-SOCIAL.href").description("link to query event list"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }

    public static ResultHandler getSocialMember() {
        return document("find-social-member",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("find-social-member").description("link to query members"),
                        linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                        fieldWithPath("social").type(JsonFieldType.STRING).description("회원 소셜 종류 판단"),
                        fieldWithPath("userId").type(JsonFieldType.STRING).description("소셜 유저 아이디")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("hal+json type")
                ),
                responseFields(
                        fieldWithPath("check").type(JsonFieldType.BOOLEAN).description("유저 존재 여부"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.find-social-member.href").description("link to query event list"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }

    public static ResultHandler getCheckEmail() {
        return document("check-member-email",
                getDocumentRequest(),
                getDocumentResponse(),
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("check-member-email").description("link to query members"),
                        linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.ARRAY).description("체크하고자 하는 유저 email")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("hal+json type")
                ),
                responseFields(
                        fieldWithPath("check").type(JsonFieldType.BOOLEAN).description("중복여부 확인"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.check-member-email.href").description("link to query event list"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }

    public static ResultHandler sendEmail() {
        return document("send-member-email",
                getDocumentRequest(),
                getDocumentResponse(),
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("send-member-email").description("link to query members"),
                        linkWithRel("profile").description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                requestFields(
                        fieldWithPath("email").type(JsonFieldType.STRING).description("체크하고자 하는 유저 email")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("hal+json type")
                ),
                responseFields(
                        fieldWithPath("check").type(JsonFieldType.BOOLEAN).description("발송여부 확인"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.send-member-email.href").description("link to query event list"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }
}
