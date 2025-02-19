package sn.zahra.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.zahra.domain.UserEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.zahra.web.rest.TestUtil;

class UserEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserEntity.class);
        UserEntity userEntity1 = getUserEntitySample1();
        UserEntity userEntity2 = new UserEntity();
        assertThat(userEntity1).isNotEqualTo(userEntity2);

        userEntity2.setId(userEntity1.getId());
        assertThat(userEntity1).isEqualTo(userEntity2);

        userEntity2 = getUserEntitySample2();
        assertThat(userEntity1).isNotEqualTo(userEntity2);
    }
}
