package com.github.kabal163.statemachine;

import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.LifecycleManager;
import com.github.kabal163.statemachine.api.TransitionsInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@ConditionalOnBean({LifecycleConfiguration.class})
@ConditionalOnProperty(prefix = "akuna.state-machine", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StateMachineAutoConfiguration {

    @Bean
    public LifecycleManager lifecycleManager(TransitionProvider transitionProvider) {
        return new LifecycleManagerImpl(transitionProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransitionsInitializer transitionsInitializer() {
        return new JavaConfigTransitionsInitializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransitionProvider transitionProvider(TransitionsInitializer transitionsInitializer,
                                                 Collection<LifecycleConfiguration> configurations) {
        return TransitionProviderImpl.builder()
                .transitionInitializer(transitionsInitializer)
                .configs(configurations)
                .build();
    }
}
