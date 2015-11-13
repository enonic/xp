package com.enonic.xp.tools.gradle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.dm.gradle.plugins.bundle.BundleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedArtifact;

final class BundleConfigurator
{
    private final static String EXPORT_PACKAGE = "Export-Package";

    private final static String IMPORT_PACKAGE = "Import-Package";

    private final static String PRIVATE_PACKAGE = "Private-Package";

    private final static String DEFAULT_IMPORT = "*;resolution:=optional";

    private final Project project;

    private final BundleExtension ext;

    public BundleConfigurator( final Project project, final BundleExtension ext )
    {
        this.project = project;
        this.ext = ext;
    }

    public void configure( final AppExtension application )
    {
        final Map<String, String> instructions = new HashMap<>();
        instructions.putAll( application.getInstructions() );

        final String exportPackage = instructions.remove( EXPORT_PACKAGE );
        instruction( "-exportcontents", exportPackage != null ? exportPackage : "" );

        final String importPackage = instructions.remove( IMPORT_PACKAGE );
        instruction( IMPORT_PACKAGE, importPackage != null ? importPackage : DEFAULT_IMPORT );
        instruction( PRIVATE_PACKAGE, "site.*;-split-package:=merge-first" );

        instruction( "-removeheaders", "Require-Capability,Include-Resource" );
        instruction( "-nouses", "true" );
        instruction( "-dsannotations", "*" );

        validateApplicationName( application.getName() );

        instruction( "Bundle-SymbolicName", application.getName() );
        instruction( "Bundle-Name", application.getDisplayName() );
        instruction( "X-Application-Url", application.getUrl() );
        instruction( "X-Vendor-Name", application.getVendorName() );
        instruction( "X-Vendor-Url", application.getVendorUrl() );
        instruction( "X-System-Version", application.getSystemVersion() );

        final Configuration libConfig = this.project.getConfigurations().getByName( "include" );
        final Configuration filteredConfig = new UnwantedJarFilter( libConfig ).filter();

        instruction( "Bundle-ClassPath", getBundleClassPath( filteredConfig ) );
        instruction( "Include-Resource", getIncludeResource( filteredConfig ) );
        includeWebJars();

        for ( final Map.Entry<String, String> entry : instructions.entrySet() )
        {
            instruction( entry.getKey(), entry.getValue() );
        }
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

    private void validateApplicationName( final String name )
    {
        if ( name.contains( "-" ) )
        {
            throw new IllegalArgumentException( "Invalid application name [" + name + "]. Name should not contain [-]." );
        }
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

    private void includeWebJars()
    {
        final File webjarsDir = new File( this.project.getBuildDir(), "webjars/META-INF/resources/webjars" );
        if ( webjarsDir.exists() )
        {
            instruction( "Include-Resource", "/site/assets=" + webjarsDir.getAbsolutePath() );
        }
    }
}
