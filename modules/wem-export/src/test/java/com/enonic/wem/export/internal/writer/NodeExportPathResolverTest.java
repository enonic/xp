package com.enonic.wem.export.internal.writer;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class NodeExportPathResolverTest
{
    @Test
    public void node_root_contains()
        throws Exception
    {
        final Path rootPath = Paths.get( "/exports" );
        final NodePath nodePath = createNodePath( "/content/my-article/image-archive" );
        final NodePath exportRootPath = createNodePath( "/content" );

        final Path relativePath = NodeExportPathResolver.resolveNodeBasePath( rootPath, nodePath, exportRootPath );

        assertEquals( Paths.get( "/exports", "my-article", "image-archive" ), relativePath );
    }

    @Test
    public void node_root_full()
        throws Exception
    {
        final Path rootPath = Paths.get( "/exports" );
        final NodePath nodePath = createNodePath( "/content/my-article/image-archive" );
        final NodePath exportRootPath = createNodePath( "/content/my-article/image-archive" );

        final Path relativePath = NodeExportPathResolver.resolveNodeBasePath( rootPath, nodePath, exportRootPath );

        assertEquals( Paths.get( "/exports" ), relativePath );
    }

    @Test(expected = IllegalArgumentException.class)
    public void node_root_not_contain()
        throws Exception
    {
        final Path rootPath = Paths.get( "/exports" );
        final NodePath nodePath = createNodePath( "/content/my-article/image-archive" );
        final NodePath exportRootPath = createNodePath( "my-article/image-archive" );

        NodeExportPathResolver.resolveNodeBasePath( rootPath, nodePath, exportRootPath );
    }

    private NodePath createNodePath( final String value )
    {
        return NodePath.newPath( value ).build();
    }
}