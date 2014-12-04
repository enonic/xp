package com.enonic.wem.export.internal.builder;

import com.google.common.base.Strings;

import com.enonic.wem.api.node.NodePath;

class ImportNodeParentPathResolver
{

    public static NodePath resolve( final String xmlNodeParentPath, final NodePath importRoot )
    {
        final NodePath.Builder newPathBuilder = NodePath.newPath( importRoot );

        String[] pathElements = xmlNodeParentPath.split( "/" );

        for ( final String pathElement : pathElements )
        {
            if ( !Strings.isNullOrEmpty( pathElement ) )
            {
                newPathBuilder.addElement( pathElement );
            }
        }

        return newPathBuilder.build();
    }

}
