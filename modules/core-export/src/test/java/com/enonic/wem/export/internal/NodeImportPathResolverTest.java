package com.enonic.wem.export.internal;

import java.nio.file.Paths;

import org.junit.Test;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFiles;

import static org.junit.Assert.*;

public class NodeImportPathResolverTest
{
    @Test
    public void resolve_into_folder()
        throws Exception
    {

        final NodePath resolvedPath =
            NodeImportPathResolver.resolveNodeImportPath( VirtualFiles.from( Paths.get( "/var", "folder", "myExport", "mynode" ) ), //
                                                          VirtualFiles.from( Paths.get( "/var", "folder", "myExport" ) ), //
                                                          NodePath.newNodePath( NodePath.ROOT, "myimport" ).build() );

        assertEquals( "/myimport/mynode", resolvedPath.toString() );
    }

    @Test
    public void resolve_child_into_folder()
        throws Exception
    {
        final NodePath resolvedPath = NodeImportPathResolver.resolveNodeImportPath(
            VirtualFiles.from( Paths.get( "/var", "folder", "myExport", "mynode", "mychild" ) ), //
            VirtualFiles.from( Paths.get( "/var", "folder", "myExport" ) ), //
            NodePath.newNodePath( NodePath.ROOT, "myimport" ).build() );

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