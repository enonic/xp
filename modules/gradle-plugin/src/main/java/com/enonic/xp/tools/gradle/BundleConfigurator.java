package com.enonic.xp.tools.gradle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.dm.gradle.plugins.bundle.BundleExtension;
import org.gradle.api.Project;

final class BundleConfigurator
{
    private final static String EXPORT_PACKAGE = "Export-Package";

    private final static String IMPORT_PACKAGE = "Import-Package";

    private final static String DEFAULT_IMPORT = "*;resolution:=optional";

    private final Project project;

    private final BundleExtension ext;

    public BundleConfigurator( final Project project, final BundleExtension ext )
    {
        this.project = project;
        this.ext = ext;
    }

    public void configure( final ModuleExtension module )
    {
        final Map<String, String> instructions = new HashMap<>();
        instructions.putAll( module.getInstructions() );

        final String exportPackage = instructions.remove( EXPORT_PACKAGE );
        instruction( "-exportcontents", exportPackage != null ? exportPackage : "" );

        final String importPackage = instructions.remove( IMPORT_PACKAGE );
        instruction( IMPORT_PACKAGE, importPackage != null ? importPackage : DEFAULT_IMPORT );

        instruction( "-removeheaders", "Require-Capability,Include-Resource" );
        instruction( "-nouses", "true" );
        instruction( "-dsannotations", "*" );

        validateModuleName( module.getName() );

        instruction( "Bundle-SymbolicName", module.getName() );
        instruction( "Bundle-Name", module.getDisplayName() );
        instruction( "X-Module-Url", module.getUrl() );
        instruction( "X-Vendor-Name", module.getVendorName() );
        instruction( "X-Vendor-Url", module.getVendorUrl() );
        instruction( "X-System-Version", module.getSystemVersion() );
        instruction( "Bundle-ClassPath", getBundleClassPath() );
        instruction( "Include-Resource", getIncludeResource() );

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

    private String getBundleClassPath()
    {
        final StringBuilder str = new StringBuilder( "." );
        for ( final File file : this.project.getConfigurations().getByName( "include" ) )
        {
            str.append( ",OSGI-INF/lib/" ).append( file.getName() );
        }

        return str.toString();
    }

    private String getIncludeResource()
    {
        final StringBuilder str = new StringBuilder( "" );
        for ( final File file : this.project.getConfigurations().getByName( "include" ) )
        {
            str.append( ",OSGI-INF/lib/" ).append( file.getName() ).append( "=" ).append( file.getPath() );
        }

        return str.length() > 0 ? str.substring( 1 ) : str.toString();
    }

    private void validateModuleName( final String name )
    {
        if ( name.contains( "-" ) )
        {
            throw new IllegalArgumentException( "Invalid module name [" + name + "]. Name should not contain [-]." );
        }
    }
}
