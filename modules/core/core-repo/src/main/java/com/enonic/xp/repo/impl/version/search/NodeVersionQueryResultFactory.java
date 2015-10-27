package com.enonic.xp.repo.impl.version.search;

import java.time.Instant;

import com.google.common.base.Strings;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

public class NodeVersionQueryResultFactory
{
    public static NodeVersionQueryResult create( final NodeVersionQuery query, final SearchResult searchResult )
    {
        final NodeVersionQueryResult.Builder findNodeVersionsResult = NodeVersionQueryResult.create();

        findNodeVersionsResult.hits( searchResult.getResults().getSize() );
        findNodeVersionsResult.totalHits( searchResult.getResults().getTotalHits() );
        findNodeVersionsResult.from( query.getFrom() );
        findNodeVersionsResult.to( query.getSize() );

        final NodeVersionsMetadata nodeVersionsMetadata = buildEntityVersions( query, searchResult );

        findNodeVersionsResult.entityVersions( nodeVersionsMetadata );

        return findNodeVersionsResult.build();
    }

    private static NodeVersionsMetadata buildEntityVersions( final NodeVersionQuery query, final SearchResult searchResult )
    {
        final NodeVersionsMetadata.Builder entityVersionsBuilder = NodeVersionsMetadata.create( query.getNodeId() );

        for ( final SearchHit searchHit : searchResult.getResults() )
        {
            entityVersionsBuilder.add( createVersionEntry( searchHit ) );
        }

        return entityVersionsBuilder.build();
    }

    private static NodeVersionMetadata createVersionEntry( final SearchHit hit )
    {
        final String timestamp = getStringValue( hit, VersionIndexPath.TIMESTAMP, true );

        final String versionId = getStringValue( hit, VersionIndexPath.VERSION_ID, true );

        final String nodePath = getStringValue( hit, VersionIndexPath.NODE_PATH, true );

        final String nodeId = getStringValue( hit, VersionIndexPath.NODE_ID, true );

        return NodeVersionMetadata.create().
            nodeVersionId( NodeVersionId.from( versionId ) ).
            timestamp( Strings.isNullOrEmpty( timestamp ) ? null : Instant.parse( timestamp ) ).
            nodePath( NodePath.create( nodePath ).build() ).
            nodeId( NodeId.from( nodeId ) ).
            build();
    }

    private static String getStringValue( final SearchHit hit, final IndexPath indexPath, final boolean required )
    {
        final ReturnValue field = hit.getField( indexPath.getPath(), required );

        if ( field == null )
        {
            return null;
        }

        return field.getSingleValue().toString();
    }


}
