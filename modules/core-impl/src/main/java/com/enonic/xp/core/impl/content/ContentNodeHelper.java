package com.enonic.xp.core.impl.content;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

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

    final static NodePath CONTENT_ROOT_NODE = NodePath.newNodePath( NodePath.ROOT, CONTENT_ROOT_NODE_NAME ).build();

    public static NodePath translateContentPathToNodePath( final ContentPath contentPath )
    {
        return new NodePath( CONTENT_ROOT_NODE_NAME + contentPath.asAbsolute().toString() ).asAbsolute().trimTrailingDivider();
    }

    public static NodePaths translateContentPathsToNodePaths( final ContentPaths contentPaths )
    {
        final NodePaths.Builder builder = NodePaths.create();
        for ( final ContentPath contentPath : contentPaths )
        {
            builder.addNodePath( ContentNodeHelper.translateContentPathToNodePath( contentPath ) );
        }

        return builder.build();
    }

    public static ContentPath translateNodePathToContentPath( final NodePath nodePath )
    {
        final String contentPath = StringUtils.substringAfter( nodePath.asAbsolute().toString(), CONTENT_ROOT_NODE_NAME + "/" );
        return ContentPath.from( contentPath );
    }

    public static NodeIds toNodeIds( final ContentIds contentIds )
    {
        final Set<NodeId> nodeIds = Sets.newHashSet();

        final Iterator<ContentId> iterator = contentIds.iterator();

        while ( iterator.hasNext() )
        {
            nodeIds.add( NodeId.from( iterator.next().toString() ) );
        }

        return NodeIds.from( nodeIds );
    }

    public static ContentIds toContentIds( final NodeIds nodeIds )
    {
        final Set<ContentId> contentIds = Sets.newHashSet();

        final Iterator<NodeId> iterator = nodeIds.iterator();

        while ( iterator.hasNext() )
        {
            contentIds.add( ContentId.from( iterator.next().toString() ) );
        }

        return ContentIds.from( contentIds );
    }

}


