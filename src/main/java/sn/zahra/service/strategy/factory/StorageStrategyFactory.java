// StorageStrategyFactory.java
package sn.zahra.service.strategy.factory;


import org.springframework.stereotype.Service;
import sn.zahra.domain.enumeration.StorageType;
import sn.zahra.service.strategy.StorageStrategy;

import java.util.List;

@Service
public class StorageStrategyFactory {

    private final List<StorageStrategy> strategies;

    public StorageStrategyFactory(List<StorageStrategy> strategies) {
        this.strategies = strategies;
    }

    public StorageStrategy getStrategy(StorageType storageType) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(storageType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported storage type: " + storageType));
    }
}

