package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentNodeHelperTest
{

    @Test
    void translateNodePathToContentPath()
    {
        assertEquals( ContentPath.from( "/site/myContent" ),
                      ContentNodeHelper.translateNodePathToContentPath( new NodePath( "/content/site/myContent" ) ) );
    }

    @Test
    void translateNodePathToContentPath_root_throws()
    {
        assertThrows( IllegalArgumentException.class,
                      () ->ContentNodeHelper.translateNodePathToContentPath( NodePath.ROOT ) );
    }

    @Test
    void translateNodePathToContentPath_archive()
    {
        assertEquals( ContentPath.from( "/site/myContent" ),
                      ContentNodeHelper.translateNodePathToContentPath( new NodePath( "/archive/site/myContent" ) ) );
    }

    @Test
    void translateContentParentToNodeParentPath()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentPathToNodePath( ContentPath.from( "/site/myContent" ) );

        assertEquals( new NodePath( "/content/site/myContent" ), nodePath );
    }

    @Test
    void translateContentPathsToNodePaths()
    {
        final ContentPaths contentPaths = ContentPaths.create().
            add( ContentPath.from( "/site/myContent" ) ).
            add( ContentPath.from( "/folder/other/content" ) ).
            build();
        final NodePaths nodePaths = ContentNodeHelper.translateContentPathsToNodePaths( contentPaths );

        assertEquals( NodePaths.from( "/content/site/myContent", "/content/folder/other/content" ), nodePaths );
    }

    @Test
    void translateNodePathsToContentPaths()
    {
        final NodePaths nodePaths = NodePaths.from( "/content/site/myContent", "/content/folder/other/content" );
        final ContentPaths contentPaths = ContentNodeHelper.translateNodePathsToContentPaths( nodePaths );

        assertEquals( ContentPaths.from( "/site/myContent", "/folder/other/content" ), contentPaths );
    }

    @Test
    void toNodeIds()
    {
        final ContentIds contentIds = ContentIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" );
        final NodeIds nodeIds = ContentNodeHelper.toNodeIds( contentIds );

        assertEquals( NodeIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" ), nodeIds );
    }

    @Test
    void toContentIds()
    {
        final NodeIds nodeIds = NodeIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" );
        final ContentIds contentIds = ContentNodeHelper.toContentIds( nodeIds );

        assertEquals( ContentIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" ), contentIds );
    }

    @Test
    void inArchive()
    {
        assertFalse( ContentNodeHelper.inArchive( NodePath.ROOT ) );
        assertFalse( ContentNodeHelper.inArchive( new NodePath( "/archive" ) ) );
        assertFalse( ContentNodeHelper.inArchive( new NodePath( "/content" ) ) );
        assertTrue( ContentNodeHelper.inArchive( new NodePath( "/archive/some/path" ) ) );
        assertFalse( ContentNodeHelper.inArchive( new NodePath( "/content/archive/some/path" ) ) );
    }
}
