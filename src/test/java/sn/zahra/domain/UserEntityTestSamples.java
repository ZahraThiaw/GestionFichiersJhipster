package sn.zahra.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserEntityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserEntity getUserEntitySample1() {
        return new UserEntity().id(1L).nom("nom1").prenom("prenom1").email("email1").password("password1");
    }

    public static UserEntity getUserEntitySample2() {
        return new UserEntity().id(2L).nom("nom2").prenom("prenom2").email("email2").password("password2");
    }

    public static UserEntity getUserEntityRandomSampleGenerator() {
        return new UserEntity()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prenom(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString());
    }
}
