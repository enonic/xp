package com.enonic.wem.export.internal;

import java.nio.file.Paths;

import org.junit.Test;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.reader.NodeImportPathException;
import com.enonic.wem.export.internal.reader.NodeImportPathResolver;

import static org.junit.Assert.*;

public class NodeImportPathResolverTest
{

    @Test(expected = NodeImportPathException.class)
    public void invalid_path_because_no_node_folder()
        throws Exception
    {
        final NodePath resolvedPath =
            NodeImportPathResolver.resolve( Paths.get( "/var/folder/myExport/_/node.xml" ), Paths.get( "/var/folder/myExport" ),
                                            NodePath.ROOT );
    }

    @Test
    public void resolve_into_folder()
        throws Exception
    {
        final NodePath resolvedPath =
            NodeImportPathResolver.resolve( Paths.get( "/var/folder/myExport/mynode/_/node.xml" ), Paths.get( "/var/folder/myExport" ),
                                            NodePath.newNodePath( NodePath.ROOT, "myimport" ).build() );

        assertEquals( "/myimport/mynode", resolvedPath.toString() );
    }

    @Test
    public void resolve_child_into_folder()
        throws Exception
    {
        final NodePath resolvedPath = NodeImportPathResolver.resolve( Paths.get( "/var/folder/myExport/mynode/mychild/_/node.xml" ),
                                                                      Paths.get( "/var/folder/myExport" ),
                                                                      NodePath.newNodePath( NodePath.ROOT, "myimport" ).build() );

        assertEquals( "/myimport/mynode/mychild", resolvedPath.toString() );
        assertEquals( "/myimport/mynode", resolvedPath.getParentPath().toString() );
    }


    @Test
    public void resolve_into_root()
        throws Exception
    {
        final NodePath resolvedPath =
            NodeImportPathResolver.resolve( Paths.get( "/var/folder/myExport/mynode/_/node.xml" ), Paths.get( "/var/folder/myExport" ),
                                            NodePath.ROOT );

        assertEquals( "/mynode", resolvedPath.toString() );
    }
}