package com.stefanopalazzo.proxy;

import com.stefanopalazzo.proxy.config.AsyncSyncConfiguration;
import com.stefanopalazzo.proxy.config.EmbeddedKafka;
import com.stefanopalazzo.proxy.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { EventosproxyApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedKafka
public @interface IntegrationTest {
}
