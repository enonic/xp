package com.enonic.xp.tools.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.MavenPlugin;

import nebula.plugin.extraconfigurations.OptionalBasePlugin;
import nebula.plugin.extraconfigurations.ProvidedBasePlugin;

public abstract class BasePlugin
    implements Plugin<Project>
{
    protected Project project;

    @Override
    public final void apply( final Project project )
    {
        this.project = project;

        this.project.getPlugins().apply( JavaPlugin.class );
        this.project.getPlugins().apply( OptionalBasePlugin.class );
        this.project.getPlugins().apply( ProvidedBasePlugin.class );
        this.project.getPlugins().apply( MavenPlugin.class );

        configure();
    }

    protected abstract void configure();
}
