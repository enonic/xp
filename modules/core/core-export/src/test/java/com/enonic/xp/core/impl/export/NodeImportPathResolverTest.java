package com.enonic.xp.core.impl.export;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeImportPathResolverTest
{
    @Test
    void resolve_into_folder()
    {

        final NodePath resolvedPath =
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Path.of( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Path.of( "/var", "folder", "myExport" ) ), //
                                                          new NodePath( NodePath.ROOT, NodeName.from( "myimport" ) ) );

        assertEquals( "/myimport/mynode", resolvedPath.toString() );
    }

    @Test
    void resolve_child_into_folder()
    {
        final NodePath resolvedPath = NodeImportPathResolver.resolveNodeImportPath(
            VirtualFiles.from( Path.of( "/var", "folder", "myExport", "mynode", "mychild" ) ), //
            VirtualFiles.from( Path.of( "/var", "folder", "myExport" ) ), //
            new NodePath( "/myimport" ) );

        assertEquals( "/myimport/mynode/mychild", resolvedPath.toString() );
        assertEquals( "/myimport/mynode", resolvedPath.getParentPath().toString() );
    }

    @Test
    void resolve_into_root()
    {
        final NodePath resolvedPath =
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Path.of( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Path.of( "/var", "folder", "myExport" ) ),//
                                                          NodePath.ROOT );

        assertEquals( "/mynode", resolvedPath.toString() );

    }

}
