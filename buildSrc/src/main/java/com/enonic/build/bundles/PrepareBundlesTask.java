package com.enonic.build.bundles;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import com.google.common.io.Files;

public class PrepareBundlesTask
    extends DefaultTask
{
    private File outputDir;

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

    @OutputDirectory
    public File getOutputDir()
    {
        return this.outputDir;
    }

    public void setOutputDir( final File value )
    {
        this.outputDir = value;
    }

    @TaskAction
    public void doExecute()
        throws Exception
    {
        final List<ResolvedBundleInfo> resolved = new BundleResolver( getExtension() ).resolve();

        writeBundlesInfo( resolved );
        if ( !this.devMode )
        {
            copyBundles( resolved );
        }
    }

    private void writeBundlesInfo( final List<ResolvedBundleInfo> resolved )
        throws Exception
    {
        final File file = new File( this.outputDir, "bundles.properties" );
        final Properties props = buildBundlesInfo( resolved );

        final FileWriter out = new FileWriter( file );
        props.store( out, "Generated list of bundles" );
        out.close();
    }

    private void copyBundles( final List<ResolvedBundleInfo> resolved )
        throws Exception
    {
        for ( final ResolvedBundleInfo info : resolved )
        {
            copyBundle( info );
        }
    }

    private void copyBundle( final ResolvedBundleInfo info )
        throws Exception
    {
        final File from = info.getArtifact().getFile();
        final File to = new File( this.outputDir, getTargetPath( info.getArtifact() ) );

        to.getParentFile().mkdirs();
        Files.copy( from, to );
    }

    private BundlesExtension getExtension()
    {
        return BundlesExtension.get( getProject() );
    }

    private Properties buildBundlesInfo( final List<ResolvedBundleInfo> resolved )
    {
        final Properties props = new Properties();
        for ( final ResolvedBundleInfo info : resolved )
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
            return artifact.getFile().toURI().toString();
        }

        return getTargetPath( artifact );
    }

    private String getTargetPath( final ResolvedArtifact artifact )
    {
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

    @Input
    private String getDetails()
    {
        return getExtension().getInfoMap().toString();
    }
}
