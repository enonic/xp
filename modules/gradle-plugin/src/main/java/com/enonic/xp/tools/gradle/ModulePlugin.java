package com.enonic.xp.tools.gradle;

import java.io.File;

import org.dm.gradle.plugins.bundle.BundleExtension;
import org.dm.gradle.plugins.bundle.BundlePlugin;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.MavenPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.bundling.Jar;

import nebula.plugin.extraconfigurations.OptionalBasePlugin;
import nebula.plugin.extraconfigurations.ProvidedBasePlugin;

import com.enonic.xp.tools.gradle.watch.WatchTask;

public class ModulePlugin
    implements Plugin<Project>
{
    private Project project;

    private ModuleExtension ext;

    @Override
    public void apply( final Project project )
    {
        this.project = project;
        this.ext = ModuleExtension.create( this.project );

        applyJavaSettings();
        applyMavenSettings();
        addLibraryConfig();
        applyDeployTask();
        applyWatcherTask();
    }

    private void applyJavaSettings()
    {
        this.project.getPlugins().apply( JavaPlugin.class );
        this.project.getPlugins().apply( BundlePlugin.class );
        this.project.getPlugins().apply( OptionalBasePlugin.class );
        this.project.getPlugins().apply( ProvidedBasePlugin.class );

        this.project.afterEvaluate( project1 -> {
            final BundleExtension ext1 = project1.getExtensions().getByType( BundleExtension.class );
            configure( ext1 );
        } );
    }

    private void configure( final BundleExtension bundle )
    {
        new BundleConfigurator( this.project, bundle ).configure( ModulePlugin.this.ext );
    }

    private void applyMavenSettings()
    {
        this.project.getPlugins().apply( MavenPlugin.class );
    }

    private void checkHomeDir()
    {
        final File dir = this.ext.getXpHome();
        if ( dir == null )
        {
            throw new GradleException( "XP_HOME is not set. Please set XP_HOME to your Enonic XP home folder." );
        }

        if ( !dir.isDirectory() )
        {
            throw new GradleException(
                "XP_HOME directory [" + dir.toString() + "] is not valid. Please ensure that XP_HOME points to a valid directory." );
        }
    }

    private void applyDeployTask()
    {
        this.project.afterEvaluate( project1 -> {
            doApplyDeployTask();
        } );
    }

    private void doApplyDeployTask()
    {
        final Copy task = this.project.getTasks().create( "deploy", Copy.class );
        task.setGroup( "Module" );
        task.setDescription( "Deploy module to XP_HOME directory." );
        task.dependsOn( this.project.getTasks().getByName( "build" ) );
        task.from( ( (Jar) this.project.getTasks().getByPath( "jar" ) ).getArchivePath() );

        final File home = this.ext.getXpHome();
        if ( home != null )
        {
            task.into( new File( home, "deploy" ) );
        }
        else
        {
            task.into( new File( this.project.getBuildDir(), "deploy" ) );
        }

        task.doFirst( task1 -> {
            checkHomeDir();
        } );
    }

    private void applyWatcherTask()
    {
        final WatchTask task = this.project.getTasks().create( "watch", WatchTask.class );
        task.setGroup( "Module" );
        task.setDescription( "Watch for changes and re-deploy jar" );
        task.setDir( new File( this.project.getProjectDir(), "src" ) );
        task.setTask( "deploy" );
    }

    private void addLibraryConfig()
    {
        final Configuration libConfig = this.project.getConfigurations().create( "include", conf -> {
            conf.setTransitive( true );
        } );

        this.project.getConfigurations().getByName( "compile" ).extendsFrom( libConfig );
    }
}
