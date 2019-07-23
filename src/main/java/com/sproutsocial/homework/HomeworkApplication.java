package com.no-namesocial.homework;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.no-namesocial.homework.util.NullStringJsonDeserializer;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Feature;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class HomeworkApplication extends Application<HomeworkConfiguration> {

    public static void main(final String[] args) throws Exception {
        new HomeworkApplication().run(args.length > 0 ? args : new String[]{"server", "config.yml"});
    }

    @Override
    public String getName() {
        return "Homework";
    }

    @Override
    public void initialize(final Bootstrap<HomeworkConfiguration> bootstrap) {
        // log exceptions from the database
        bootstrap.addBundle(new JdbiExceptionsBundle());
    }

    @Override
    public void run(final HomeworkConfiguration configuration, final Environment environment) {
        final JdbiFactory factory = new JdbiFactory();
        final Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "sqlite");
        final AccessTokenService tokenService = new AccessTokenService(jdbi);

        final Client client = new JerseyClientBuilder(environment)
                .using(configuration.getJerseyClientConfiguration())
                .build(getName());
        client.register(JacksonFeature.class);

        final ConsumerCredentials appCredentials = new ConsumerCredentials(
                configuration.getTwitterConsumerKey(), configuration.getTwitterConsumerSecret());
        final Feature oauthFeature = OAuth1ClientSupport.builder(appCredentials).feature().build();
        client.register(oauthFeature);

        environment.jersey().register(new TwitterResource(client, configuration.getTwitterEndpoints(), tokenService));
        environment.healthChecks().register("twitter",
                new TwitterHealthCheck(client, configuration.getFunctionalUserId(), tokenService));

        // json deserialization
        environment.getObjectMapper().registerModule(
                new SimpleModule().addDeserializer(String.class, new NullStringJsonDeserializer()));
        environment.getObjectMapper().registerModule(new JodaModule());
    }
}
