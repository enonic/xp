package com.enonic.xp.core.impl.export;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeImportPathResolverTest
{
    @Test
    public void resolve_into_folder()
        throws Exception
    {

        final NodePath resolvedPath =
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Path.of( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Path.of( "/var", "folder", "myExport" ) ), //
                                                          new NodePath( NodePath.ROOT, NodeName.from( "myimport" ) ) );

        assertEquals( "/myimport/mynode", resolvedPath.toString() );
    }

    @Test
    public void resolve_child_into_folder()
        throws Exception
    {
        final NodePath resolvedPath = NodeImportPathResolver.resolveNodeImportPath(
            VirtualFiles.from( Path.of( "/var", "folder", "myExport", "mynode", "mychild" ) ), //
            VirtualFiles.from( Path.of( "/var", "folder", "myExport" ) ), //
            new NodePath( "/myimport" ) );

        assertEquals( "/myimport/mynode/mychild", resolvedPath.toString() );
        assertEquals( "/myimport/mynode", resolvedPath.getParentPath().toString() );
    }

    @Test
    public void resolve_into_root()
        throws Exception
    {
        final NodePath resolvedPath =
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Path.of( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Path.of( "/var", "folder", "myExport" ) ),//
                                                          NodePath.ROOT );

        assertEquals( "/mynode", resolvedPath.toString() );

    }

}
