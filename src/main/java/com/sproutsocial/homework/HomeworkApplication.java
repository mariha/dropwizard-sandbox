package com.no-namesocial.homework;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.skife.jdbi.v2.DBI;

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
    }

    @Override
    public void run(final HomeworkConfiguration configuration, final Environment environment) {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "sqlite");

        final ConsumerCredentials appCredentials = new ConsumerCredentials(
                configuration.getTwitterConsumerKey(), configuration.getTwitterConsumerSecret());
        final Feature filterFeature = OAuth1ClientSupport.builder(appCredentials).feature().build();

        final Client client = new JerseyClientBuilder(environment).build(getName());
        client.register(filterFeature);
        client.register(JacksonFeature.class);

        environment.jersey().register(new TwitterResource(configuration, client));
        environment.healthChecks().register("twitter", new TwitterHealthCheck(configuration, client));
    }
}
