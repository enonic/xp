package com.enonic.build.bundles;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public class BundlesPropertiesTask
    extends DefaultTask
{
    private File outputFile;

    private boolean devMode = false;

    @Input
    public boolean getDevMode()
    {
        return this.devMode;
    }

    public void setDevMode( final boolean value )
    {
        this.devMode = value;
    }

    @OutputFile
    public File getOutputFile()
    {
        return this.outputFile;
    }

    public void setOutputDir( final File value )
    {
        this.outputFile = new File( value, "bundles.properties" );
    }

    @TaskAction
    public void doExecute()
        throws Exception
    {
        final Properties props = buildProperties();
        final FileWriter out = new FileWriter( this.outputFile );
        props.store( out, "Generated list of bundles" );
        out.close();
    }

    private BundlesExtension getExtension()
    {
        return BundlesExtension.get( getProject() );
    }

    private Properties buildProperties()
    {
        final Properties props = new Properties();
        for ( final ResolvedBundleInfo info : new BundleResolver( getExtension() ).resolve() )
        {
            addBundle( props, info );
        }

        return props;
    }

    private void addBundle( final Properties props, final ResolvedBundleInfo info )
    {
        final String uri = resolveUri( info.getArtifact() );
        props.put( uri, String.valueOf( info.getLevel() ) );
    }

    private String resolveUri( final ResolvedArtifact artifact )
    {
        if ( this.devMode )
        {
            return resolveUri( artifact.getFile() );
        }

        final StringBuilder str = new StringBuilder();
        str.append( artifact.getModuleVersion().getId().getGroup().replace( '.', '/' ) );
        str.append( '/' ).append( artifact.getModuleVersion().getId().getName() );
        str.append( '/' ).append( artifact.getModuleVersion().getId().getVersion() );
        str.append( '/' ).append( artifact.getModuleVersion().getId().getName() );
        str.append( '-' ).append( artifact.getModuleVersion().getId().getVersion() );

        if ( artifact.getClassifier() != null )
        {
            str.append( '-' ).append( artifact.getClassifier() );
        }

        str.append( '.' ).append( artifact.getExtension() );
        return str.toString();
    }

    private String resolveUri( final File file )
    {
        return file.toURI().toString();
    }

    @Input
    private String getDetails()
    {
        return getExtension().getInfoMap().toString();
    }
}
