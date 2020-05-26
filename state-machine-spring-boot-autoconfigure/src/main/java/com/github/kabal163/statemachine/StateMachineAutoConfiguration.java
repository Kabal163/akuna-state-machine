package com.github.kabal163.statemachine;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.kabal163.statemachine.api.LifecycleConfiguration;
import com.github.kabal163.statemachine.api.LifecycleManager;

@Configuration
@ConditionalOnProperty(prefix = "akuna.state-machine", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StateMachineAutoConfiguration {

    @Bean
    @ConditionalOnBean({LifecycleConfiguration.class})
    public <S, E> LifecycleManager<?, ?> lifecycleManager(TransitionBuilder<S, E> transitionBuilder,
                                                          LifecycleConfiguration<S, E> lifecycleConfiguration) {
        LifecycleManagerImpl<S, E> lifecycleManager = new LifecycleManagerImpl<>(transitionBuilder, lifecycleConfiguration);
        lifecycleManager.init();

        return lifecycleManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public TransitionBuilder<?, ?> transitionBuilder() {
        return new TransitionBuilderImpl<>();
    }
}
