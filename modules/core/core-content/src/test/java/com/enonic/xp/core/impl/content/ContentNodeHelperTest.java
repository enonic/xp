package com.enonic.xp.core.impl.content;

import org.junit.Test;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

import static org.junit.Assert.*;

public class ContentNodeHelperTest
{

    @Test
    public void translateNodePathToContentPath()
    {
        final ContentPath contentPath =
            ContentNodeHelper.translateNodePathToContentPath( NodePath.create( "/content/site/myContent" ).build() );

        assertEquals( ContentPath.from( "/site/myContent" ), contentPath );
    }

    @Test
    public void translateContentParentToNodeParentPath()
    {
        final NodePath nodePath = ContentNodeHelper.translateContentParentToNodeParentPath( ContentPath.from( "/site/myContent" ) );

        assertEquals( NodePath.create( "/content/site/myContent" ).build(), nodePath );
    }

    @Test
    public void translateContentPathsToNodePaths()
    {
        final ContentPaths contentPaths = ContentPaths.create().
            add( ContentPath.from( "/site/myContent" ) ).
            add( ContentPath.from( "/folder/other/content" ) ).
            build();
        final NodePaths nodePaths = ContentNodeHelper.translateContentPathsToNodePaths( contentPaths );

        assertEquals( NodePaths.from( "/content/site/myContent", "/content/folder/other/content" ), nodePaths );
    }

    @Test
    public void translateNodePathsToContentPaths()
    {
        final NodePaths nodePaths = NodePaths.from( "/content/site/myContent", "/content/folder/other/content" );
        final ContentPaths contentPaths = ContentNodeHelper.translateNodePathsToContentPaths( nodePaths );

        assertEquals( ContentPaths.from( "/site/myContent", "/folder/other/content" ), contentPaths );
    }

    @Test
    public void toNodeIds()
    {
        final ContentIds contentIds = ContentIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" );
        final NodeIds nodeIds = ContentNodeHelper.toNodeIds( contentIds );

        assertEquals( NodeIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" ), nodeIds );
    }

    @Test
    public void toContentIds()
    {
        final NodeIds nodeIds = NodeIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" );
        final ContentIds contentIds = ContentNodeHelper.toContentIds( nodeIds );

        assertEquals( ContentIds.from( "e1f57280-d672-4cd8-b674-98e26e5b69ae", "45d67001-7f2b-4093-99ae-639be9fdd1f6" ), contentIds );
    }

}