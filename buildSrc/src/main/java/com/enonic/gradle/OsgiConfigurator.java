package com.enonic.gradle;

import org.dm.gradle.plugins.bundle.BundleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;

public final class OsgiConfigurator
{
    private final Project project;

    private BundleExtension ext;

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
        this.ext = this.project.getExtensions().getByType( BundleExtension.class );

        final Configuration libConfig = this.project.getConfigurations().getByName( "include" );
        instruction( "Bundle-ClassPath", getBundleClassPath( libConfig ) );
        instruction( "Include-Resource", getIncludeResource( libConfig ) );
    }

    private void instruction( final String name, final Object value )
    {
        if ( value != null )
        {
            this.ext.instruction( name, value.toString() );
        }
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
