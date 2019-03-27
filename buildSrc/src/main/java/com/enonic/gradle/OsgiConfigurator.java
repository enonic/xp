package com.enonic.gradle;

import java.util.Collections;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.java.archives.Manifest;
import org.gradle.jvm.tasks.Jar;

public final class OsgiConfigurator
{
    private final Project project;

    public OsgiConfigurator( final Project project )
    {
        this.project = project;
    }

    private void addLibraryConfig()
    {
        final Configuration libConfig = this.project.getConfigurations().create( "include", conf -> {
            conf.setTransitive( true );
        } );

        this.project.getConfigurations().getByName( "compile" ).extendsFrom( libConfig );
    }

    private void afterConfigure()
    {
        final Jar jar = (Jar) this.project.getProperties().get( "jar" );
        final Manifest manifest = jar.getManifest();

        final Configuration libConfig = this.project.getConfigurations().getByName( "include" );
        manifest.attributes( Collections.singletonMap( "Bundle-ClassPath", getBundleClassPath( libConfig ) ) );
        manifest.attributes( Collections.singletonMap( "Include-Resource", getIncludeResource( libConfig ) ) );
    }

    private String getBundleClassPath( final Configuration config )
    {
        final StringBuilder str = new StringBuilder( "." );
        for ( final ResolvedArtifact artifact : config.getResolvedConfiguration().getResolvedArtifacts() )
        {
            str.append( ",OSGI-INF/lib/" ).append( getFileName( artifact ) );
        }

        return str.toString();
    }

    private String getIncludeResource( final Configuration config )
    {
        final StringBuilder str = new StringBuilder( "" );
        for ( final ResolvedArtifact artifact : config.getResolvedConfiguration().getResolvedArtifacts() )
        {
            final String name = getFileName( artifact );
            str.append( ",OSGI-INF/lib/" ).append( name ).append( "=" ).append( artifact.getFile().getPath() );
        }

        return str.length() > 0 ? str.substring( 1 ) : str.toString();
    }

    private String getFileName( final ResolvedArtifact artifact )
    {
        final ModuleVersionIdentifier id = artifact.getModuleVersion().getId();
        String name = id.getName() + "-" + id.getVersion();

        if ( artifact.getClassifier() != null )
        {
            name += "-" + artifact.getClassifier();
        }

        return name + "." + artifact.getExtension();
    }

    public static void configure( final Project project )
    {
        final OsgiConfigurator conf = new OsgiConfigurator( project );
        conf.addLibraryConfig();

        project.afterEvaluate( p -> {
            conf.afterConfigure();
        } );
    }
}
