package sn.zahra.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.zahra.web.rest.TestUtil;

class FileEntityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileEntityDTO.class);
        FileEntityDTO fileEntityDTO1 = new FileEntityDTO();
        fileEntityDTO1.setId(1L);
        FileEntityDTO fileEntityDTO2 = new FileEntityDTO();
        assertThat(fileEntityDTO1).isNotEqualTo(fileEntityDTO2);
        fileEntityDTO2.setId(fileEntityDTO1.getId());
        assertThat(fileEntityDTO1).isEqualTo(fileEntityDTO2);
        fileEntityDTO2.setId(2L);
        assertThat(fileEntityDTO1).isNotEqualTo(fileEntityDTO2);
        fileEntityDTO1.setId(null);
        assertThat(fileEntityDTO1).isNotEqualTo(fileEntityDTO2);
    }
}
