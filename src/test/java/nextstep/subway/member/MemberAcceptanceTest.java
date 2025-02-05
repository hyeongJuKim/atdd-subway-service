package nextstep.subway.member;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.로그인_성공;
import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.로그인_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.member.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MemberAcceptanceTest extends AcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;
    public final static String UN_REGISTERED_EMAIL = "no@gmail.com";
    public final static String NOT_MATCH_PASSWORD = "0000";

    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> findResponse = 회원_정보_조회_요청(createResponse);
        // then
        회원_정보_조회됨(findResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);
        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);
        // then
        회원_삭제됨(deleteResponse);
    }

    /**
     * Feature: 나의 정보 관리 기능
     *
     * Background
     *  Given 회원 등록되어 있음
     *  And 로그인 되어 있음
     *
     * Scenario: 나의 정보를 조회, 수정, 삭제한다.
     *  When 내 정보 조회 요청
     *  Then 내 정보 조회 됨
     *
     *  When 내 정보 수정 요청
     *  Then 내 정보 수정 됨
     *
     *  When 내 정보 삭제 요청
     *  Then 내 정보 삭제 됨
     *
     */
    @DisplayName("나의 정보를 관리한다.")
    @Test
    void manageMyInfo() {
        // Given 회원 등록되어 있음
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createResponse);

        // And 로그인 되어 있음
        ExtractableResponse<Response> loginResponse = 로그인_요청(new TokenRequest(EMAIL, PASSWORD));
        로그인_성공(loginResponse);
        String accessToken = loginResponse.as(TokenResponse.class).getAccessToken();

        // When 내 정보 조회 요청
        ExtractableResponse<Response> myInfoResponse = 내_정보_조회_요청(accessToken);
        // Then 내 정보 조회 됨
        내_정보_조회됨(myInfoResponse, EMAIL, AGE);

        // When 내 정보 수정 요청
        ExtractableResponse<Response> myInfoPutResponse = 내_정보_수정_요청(accessToken,
                new MemberRequest(NEW_EMAIL, NEW_PASSWORD, NEW_AGE));

        // Then 내 정보 수정 됨
        내_정보_수정됨(myInfoPutResponse);

        // When 내 정보 삭제 요청
        ExtractableResponse<Response> myInfoDeleteResponse = 내_정보_삭제_요청(accessToken);

        // Then 내 정보 삭제 됨
        내_정보_삭제됨(myInfoDeleteResponse);

    }

    public static ExtractableResponse<Response> 회원_생성을_요청(String email, String password, Integer age) {
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");

        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_정보_수정_요청(ExtractableResponse<Response> response, String email, String password, Integer age) {
        String uri = response.header("Location");
        MemberRequest memberRequest = new MemberRequest(email, password, age);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원_삭제_요청(ExtractableResponse<Response> response) {
        String uri = response.header("Location");
        return RestAssured
                .given().log().all()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static void 회원_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 회원_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);
        assertThat(memberResponse.getId()).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(email);
        assertThat(memberResponse.getAge()).isEqualTo(age);
    }

    public static void 회원_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 회원_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> 내_정보_조회_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/members/me")
                .then().log().all()
                .extract();
    }

    private void 내_정보_조회됨(ExtractableResponse<Response> response, String email, int age) {
        MemberResponse memberResponse = response.as(MemberResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(memberResponse.getId()).isNotNull(),
                () -> assertThat(memberResponse.getEmail()).isEqualTo(email),
                () -> assertThat(memberResponse.getAge()).isEqualTo(age)
        );

    }

    public static void 내_정보_조회_실패(ExtractableResponse<Response> response, String expectedErrorMessage) {
        String errorMessage = response.body().path("message").toString();
        assertAll(
                () -> assertThat(errorMessage).isEqualTo(expectedErrorMessage),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        );
    }

    private ExtractableResponse<Response> 내_정보_수정_요청(String accessToken, MemberRequest memberRequest) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberRequest)
                .when().put("/members/me")
                .then().log().all()
                .extract();
    }

    private void 내_정보_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 내_정보_삭제_요청(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .when().delete("/members/me")
                .then().log().all()
                .extract();
    }

    private void 내_정보_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
