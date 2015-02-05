package com.enonic.build.bundles;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaBasePlugin;

public class BundlesPlugin
    implements Plugin<Project>
{
    private Project project;

    @Override
    public void apply( final Project project )
    {
        this.project = project;

        project.getPlugins().apply( JavaBasePlugin.class );
        createExtension();
        createConfiguration();

        newPrepareBundlesTask( "prepareProdBundles", "Generate bundles.properties and copies the bundles.", false );
        newPrepareBundlesTask( "prepareDevBundles", "Generate bundles.properties for development.", true );
    }

    private void createExtension()
    {
        BundlesExtension.create( this.project );
    }

    private void createConfiguration()
    {
        final Configuration config = this.project.getConfigurations().create( BundlesExtension.CONFIG_NAME );
        config.setTransitive( false );
    }

    private PrepareBundlesTask newPrepareBundlesTask( final String name, final String description, final boolean devMode )
    {
        final PrepareBundlesTask task = project.getTasks().create( name, PrepareBundlesTask.class );
        task.setGroup( "bundles" );
        task.setDevMode( devMode );
        task.setDescription( description );
        return task;
    }
}
