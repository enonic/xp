package com.enonic.gradle

import aQute.bnd.osgi.Constants
import org.dm.gradle.plugins.bundle.BundleExtension
import org.gradle.api.Action
import org.gradle.api.Project

class OsgiConfiguration
    implements Action<Project>, Constants
{
    private Project project

    private ModuleExtension ext

    private BundleExtension bundle

    @Override
    void execute( final Project project )
    {
        this.project = project
        this.ext = ModuleExtension.get( this.project )
        this.bundle = this.project.extensions.getByType( BundleExtension )

        this.project.afterEvaluate {
            applyBundleSettings()
        }
    }

    private void applyBundleSettings()
    {
        setBundleInstruction( BUNDLE_NAME, this.ext.displayName )
        setBundleInstruction( BUNDLE_VENDOR, this.ext.vendor )
        setBundleInstructions( EXPORT_PACKAGE, this.ext.exportPackages )
        setBundleInstructions( IMPORT_PACKAGE, this.ext.importPackages )

        setBundleInstruction( INCLUDE_RESOURCE, "@jdom2-2.0.5.jar!/META-INF/jdom-info.xml" )
    }

    private void setBundleInstruction( final String name, final String value )
    {
        if ( value == null )
        {
            return
        }

        this.bundle.instruction( name, value )
    }

    private void setBundleInstructions( final String name, final String[] values )
    {
        if ( values == null )
        {
            return
        }

        values.each {
            this.bundle.instruction( name, it )
        }
    }
}
