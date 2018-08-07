package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

class ContentNodeHelper
{
    private final static String CONTENT_ROOT_NODE_NAME = "content";

    final static NodePath CONTENT_ROOT_NODE = NodePath.create( NodePath.ROOT, CONTENT_ROOT_NODE_NAME ).build();

    public static NodePath translateContentPathToNodePath( final ContentPath contentPath )
    {
        return new NodePath( CONTENT_ROOT_NODE_NAME + contentPath.asAbsolute().toString() ).asAbsolute().trimTrailingDivider();
    }

    public static NodePaths translateContentPathsToNodePaths( final ContentPaths contentPaths )
    {
        final NodePaths.Builder builder = NodePaths.create();

        builder.addNodePaths(
            contentPaths.stream().map( ( ContentNodeHelper::translateContentPathToNodePath ) ).collect( Collectors.toList() ) );

        return builder.build();
    }

    public static ContentPaths translateNodePathsToContentPaths( final NodePaths nodePaths )
    {
        return ContentPaths.from(
            nodePaths.stream().map( ( ContentNodeHelper::translateNodePathToContentPath ) ).collect( Collectors.toList() ) );
    }

    public static ContentPath translateNodePathToContentPath( final NodePath nodePath )
    {
        final String contentPath = StringUtils.substringAfter( nodePath.asAbsolute().toString(), CONTENT_ROOT_NODE_NAME + "/" );
        return ContentPath.from( contentPath ).asAbsolute();
    }

    public static NodePath translateContentParentToNodeParentPath( final ContentPath parentContentPath )
    {
        return NodePath.create( ContentConstants.CONTENT_ROOT_PATH ).elements( parentContentPath.toString() ).build();
    }

    public static NodeIds toNodeIds( final ContentIds contentIds )
    {
        return NodeIds.from( contentIds.stream().
            map( contentId -> NodeId.from( contentId.toString() ) ).
            collect( Collectors.toList() ) );
    }

    public static ContentIds toContentIds( final NodeIds nodeIds )
    {
        return ContentIds.from( nodeIds.stream().
            map( nodeId -> ContentId.from( nodeId.toString() ) ).
            collect( Collectors.toList() ) );
    }

}


