package nextstep.subway.auth.acceptance;

import nextstep.subway.auth.domain.LoginMember;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginMember는 도메인 테스트")
class LoginMemberTest {

    @DisplayName("ID, 이메일이 같으면 LoginMember는 동등하다.")
    @Test
    void equal(){
        LoginMember member1 = new LoginMember(1L, "aaa@gmail.com", 10);
        LoginMember member2 = new LoginMember(1L, "aaa@gmail.com", 10);

        Assertions.assertThat(member1).isEqualTo(member2);
    }

    @DisplayName("ID가 다르면 LoginMember는 동등하지 않다.")
    @Test
    void notEqual1(){
        LoginMember member1 = new LoginMember(1L, "aaa@gmail.com", 10);
        LoginMember member2 = new LoginMember(2L, "aaa@gmail.com", 10);

        Assertions.assertThat(member1).isNotEqualTo(member2);
    }

    @DisplayName("이메일이 다르면 LoginMember는 동등하지 않다.")
    @Test
    void notEqual2(){
        LoginMember member1 = new LoginMember(1L, "aaa@gmail.com", 10);
        LoginMember member2 = new LoginMember(1L, "bbb@gmail.com", 10);

        Assertions.assertThat(member1).isNotEqualTo(member2);
    }
}
