package sn.zahra.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FileEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FileEntity getFileEntitySample1() {
        return new FileEntity()
            .id(1L)
            .fileName("fileName1")
            .originalFileName("originalFileName1")
            .contentType("contentType1")
            .fileSize(1L)
            .filePath("filePath1");
    }

    public static FileEntity getFileEntitySample2() {
        return new FileEntity()
            .id(2L)
            .fileName("fileName2")
            .originalFileName("originalFileName2")
            .contentType("contentType2")
            .fileSize(2L)
            .filePath("filePath2");
    }

    public static FileEntity getFileEntityRandomSampleGenerator() {
        return new FileEntity()
            .id(longCount.incrementAndGet())
            .fileName(UUID.randomUUID().toString())
            .originalFileName(UUID.randomUUID().toString())
            .contentType(UUID.randomUUID().toString())
            .fileSize(longCount.incrementAndGet())
            .filePath(UUID.randomUUID().toString());
    }
}
