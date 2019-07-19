package com.no-namesocial.homework;

import io.dropwizard.Application;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.skife.jdbi.v2.DBI;

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

//        final Client client = new JerseyClientBuilder().build();
        final HttpClient client = new HttpClientBuilder(environment).build(getName());
        environment.jersey().register(new TwitterResource(client));
    }
}
