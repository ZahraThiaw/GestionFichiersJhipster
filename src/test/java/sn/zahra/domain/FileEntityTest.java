package sn.zahra.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.zahra.domain.FileEntityTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.zahra.web.rest.TestUtil;

class FileEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FileEntity.class);
        FileEntity fileEntity1 = getFileEntitySample1();
        FileEntity fileEntity2 = new FileEntity();
        assertThat(fileEntity1).isNotEqualTo(fileEntity2);

        fileEntity2.setId(fileEntity1.getId());
        assertThat(fileEntity1).isEqualTo(fileEntity2);

        fileEntity2 = getFileEntitySample2();
        assertThat(fileEntity1).isNotEqualTo(fileEntity2);
    }
}
