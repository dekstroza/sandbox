package io.dekstroza.github.examples.common.config;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;

public class Settings extends AbstractExtensionId<SettingsImpl> {

    public final static Settings SettingsProvider = new Settings();

    @Override
    public SettingsImpl createExtension(ExtendedActorSystem system) {
        return new SettingsImpl(system.settings().config());
    }

    public Settings lookup() {
        return Settings.SettingsProvider;
    }
}
