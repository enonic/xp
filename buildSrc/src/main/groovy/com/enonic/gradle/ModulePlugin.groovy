package com.enonic.gradle

import org.dm.gradle.plugins.bundle.BundlePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class ModulePlugin
    implements Plugin<Project>
{
    @Override
    void apply( final Project project )
    {
        project.plugins.apply( BundlePlugin )
        ModuleExtension.create( project )

        new OsgiConfiguration().execute( project )
    }
}
