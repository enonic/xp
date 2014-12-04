package com.enonic.wem.export.internal.builder;

import org.junit.Test;

import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class ImportNodeParentPathResolverTest
{
    @Test
    public void root()
        throws Exception
    {
        final NodePath newParentPath = ImportNodeParentPathResolver.resolve( "/", NodePath.ROOT );

        assertEquals( NodePath.ROOT, newParentPath );
    }

    @Test
    public void has_parent()
        throws Exception
    {
        final NodePath newParentPath = ImportNodeParentPathResolver.resolve( "/content", NodePath.ROOT );

        assertEquals( NodePath.newPath( "/content" ).build(), newParentPath );
    }

    @Test
    public void add_to_import_root()
        throws Exception
    {
        final NodePath newParentPath =
            ImportNodeParentPathResolver.resolve( "/image-archive", NodePath.newNodePath( NodePath.ROOT, "myimportfolder" ).build() );

        assertEquals( NodePath.newPath( "/myimportfolder/image-archive" ).build(), newParentPath );
    }
}