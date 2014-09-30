package com.enonic.wem.core.content;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodePaths;

class ContentNodeHelper
{
    private final static String CONTENT_ROOT_NODE_NAME = "content";

    final static NodePath CONTENT_ROOT_NODE = NodePath.newNodePath( NodePath.ROOT, CONTENT_ROOT_NODE_NAME ).build();

    public static NodePath translateContentPathToNodePath( final ContentPath contentPath )
    {
        return new NodePath( CONTENT_ROOT_NODE_NAME + "/" + contentPath.toString() ).asAbsolute();
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

}


