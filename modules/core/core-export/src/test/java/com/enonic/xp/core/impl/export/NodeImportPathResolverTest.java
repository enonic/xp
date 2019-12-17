package com.enonic.xp.core.impl.export;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

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
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Paths.get( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Paths.get( "/var", "folder", "myExport" ) ), //
                                                          NodePath.create( NodePath.ROOT, "myimport" ).build() );

        assertEquals( "/myimport/mynode", resolvedPath.toString() );
    }

    @Test
    public void resolve_child_into_folder()
        throws Exception
    {
        final NodePath resolvedPath = NodeImportPathResolver.resolveNodeImportPath(
            VirtualFiles.from( Paths.get( "/var", "folder", "myExport", "mynode", "mychild" ) ), //
            VirtualFiles.from( Paths.get( "/var", "folder", "myExport" ) ), //
            NodePath.create( NodePath.ROOT, "myimport" ).build() );

        assertEquals( "/myimport/mynode/mychild", resolvedPath.toString() );
        assertEquals( "/myimport/mynode", resolvedPath.getParentPath().toString() );
    }

    @Test
    public void resolve_into_root()
        throws Exception
    {
        final NodePath resolvedPath =
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Paths.get( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Paths.get( "/var", "folder", "myExport" ) ),//
                                                          NodePath.ROOT );

        assertEquals( "/mynode", resolvedPath.toString() );

    }

}
