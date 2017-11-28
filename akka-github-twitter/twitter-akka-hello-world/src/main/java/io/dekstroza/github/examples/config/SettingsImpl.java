package io.dekstroza.github.examples.config;

import akka.actor.Extension;
import com.typesafe.config.Config;

public class SettingsImpl implements Extension {

    public final String CONSUMER_KEY;
    public final String CONSUMER_SECRET;

    public SettingsImpl(Config config) {
        CONSUMER_KEY = config.getString("twitter.consumer.key");
        CONSUMER_SECRET = config.getString("twitter.consumer.secret");
    }
}
