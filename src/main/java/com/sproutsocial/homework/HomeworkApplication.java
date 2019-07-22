package com.no-namesocial.homework;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.jdbi.v3.core.Jdbi;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Feature;

public class HomeworkApplication extends Application<HomeworkConfiguration> {

    public static void main(final String[] args) throws Exception {
        new HomeworkApplication().run(args);
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

        final Client client = new JerseyClientBuilder(environment)
                .using(configuration.getJerseyClientConfiguration())
                .build(getName());
        client.register(JacksonFeature.class);

        final ConsumerCredentials appCredentials = new ConsumerCredentials(
                configuration.getTwitterConsumerKey(), configuration.getTwitterConsumerSecret());
        final Feature oauthFeature = OAuth1ClientSupport.builder(appCredentials).feature().build();
        client.register(oauthFeature);

        environment.jersey().register(new TwitterResource("https://api.twitter.com/1.1", client, new AccessTokenService(jdbi)));
        environment.healthChecks().register("twitter", new TwitterHealthCheck(configuration, client));
    }
}
