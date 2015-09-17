package com.enonic.xp.tools.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;

public abstract class BasePlugin
    implements Plugin<Project>
{
    protected Project project;

    @Override
    public final void apply( final Project project )
    {
        this.project = project;
        this.project.getPlugins().apply( JavaPlugin.class );
        configure();
    }

    protected abstract void configure();
}
