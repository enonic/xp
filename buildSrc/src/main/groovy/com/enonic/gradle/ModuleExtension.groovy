package com.enonic.gradle

import aQute.bnd.osgi.Constants
import org.gradle.api.Project

class ModuleExtension
    implements Constants
{
    final static String NAME = 'module'

    def String displayName

    def String vendor

    def String[] exportPackages

    def String[] importPackages

    def String[] embeddedDependencies

    static ModuleExtension get( final Project project )
    {
        return project.extensions.getByType( ModuleExtension )
    }

    static ModuleExtension create( final Project project )
    {
        return project.extensions.create( NAME, ModuleExtension )
    }
}
