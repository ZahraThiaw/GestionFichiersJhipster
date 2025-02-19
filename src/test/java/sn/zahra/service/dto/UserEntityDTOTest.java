package sn.zahra.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.zahra.web.rest.TestUtil;

class UserEntityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserEntityDTO.class);
        UserEntityDTO userEntityDTO1 = new UserEntityDTO();
        userEntityDTO1.setId(1L);
        UserEntityDTO userEntityDTO2 = new UserEntityDTO();
        assertThat(userEntityDTO1).isNotEqualTo(userEntityDTO2);
        userEntityDTO2.setId(userEntityDTO1.getId());
        assertThat(userEntityDTO1).isEqualTo(userEntityDTO2);
        userEntityDTO2.setId(2L);
        assertThat(userEntityDTO1).isNotEqualTo(userEntityDTO2);
        userEntityDTO1.setId(null);
        assertThat(userEntityDTO1).isNotEqualTo(userEntityDTO2);
    }
}
