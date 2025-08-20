package com.enonic.xp.core.impl.content;

import java.util.Objects;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

import static com.enonic.xp.archive.ArchiveConstants.ARCHIVE_ROOT_NAME;
import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_NAME;
import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;

public class ContentNodeHelper
{
    public static NodePath translateContentPathToNodePath( final ContentPath contentPath )
    {
        return translateContentPathToNodePath( getContentRoot(), contentPath );
    }

    public static NodePath translateContentPathToNodePath( final NodePath contentRoot, final ContentPath contentPath )
    {
        final NodePath.Builder builder = NodePath.create( contentRoot );

        for ( final ContentName contentName : contentPath )
        {
            builder.addElement( NodeName.from( contentName ) );
        }
        return builder.build();
    }

    public static NodePaths translateContentPathsToNodePaths( final ContentPaths contentPaths )
    {
        return contentPaths.stream().map( ContentNodeHelper::translateContentPathToNodePath ).collect( NodePaths.collector() );
    }

    public static ContentPaths translateNodePathsToContentPaths( final NodePaths nodePaths )
    {
        return nodePaths.stream().map( ContentNodeHelper::translateNodePathToContentPath ).collect( ContentPaths.collector() );
    }

    public static ContentPath translateNodePathToContentPath( final NodePath nodePath )
    {
        final String pathString = nodePath.toString();
        final int endIndex = pathString.indexOf( '/', 1 );
        final String rootNodeName = pathString.substring( 1, endIndex == -1 ? pathString.length() : endIndex );
        if ( CONTENT_ROOT_NAME.equals( rootNodeName ) || ARCHIVE_ROOT_NAME.equals( rootNodeName ) )
        {
            return endIndex == -1 ? ContentPath.ROOT : ContentPath.from( pathString.substring( endIndex ) );
        }

        throw new IllegalArgumentException( "Node path is not a content path: " + nodePath );
    }

    public static boolean inArchive( final NodePath nodePath )
    {
        return nodePath.toString().startsWith("/" + ARCHIVE_ROOT_NAME + "/");
    }

    public static NodeIds toNodeIds( final Iterable<ContentId> contentIds )
    {
        final NodeIds.Builder builder = NodeIds.create();
        for ( final ContentId contentId : contentIds )
        {
            builder.add( NodeId.from( contentId ) );
        }
        return builder.build();
    }

    public static ContentIds toContentIds( final Iterable<NodeId> nodeIds )
    {
        final ContentIds.Builder builder = ContentIds.create();
        for ( NodeId nodeId : nodeIds )
        {
            builder.add( ContentId.from( nodeId ) );
        }
        return builder.build();
    }

    public static NodePath getContentRoot()
    {
        return Objects.requireNonNullElse( (NodePath) ContextAccessor.current().getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ),
                                           ContentConstants.CONTENT_ROOT_PATH );
    }
}


