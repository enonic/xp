package com.enonic.wem.repo.internal.version;

import java.time.Instant;

import com.google.common.base.Strings;

import com.enonic.wem.repo.internal.storage.result.ReturnValue;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersions;

public class NodeVersionQueryResultFactory
{
    public static NodeVersionQueryResult create( final NodeVersionQuery query, final SearchResult searchResult )
    {
        final NodeVersionQueryResult.Builder findNodeVersionsResult = NodeVersionQueryResult.create();

        findNodeVersionsResult.hits( searchResult.getResults().getSize() );
        findNodeVersionsResult.totalHits( searchResult.getResults().getTotalHits() );
        findNodeVersionsResult.from( query.getFrom() );
        findNodeVersionsResult.to( query.getSize() );

        final NodeVersions nodeVersions = buildEntityVersions( query, searchResult );

        findNodeVersionsResult.entityVersions( nodeVersions );

        return findNodeVersionsResult.build();
    }

    private static NodeVersions buildEntityVersions( final NodeVersionQuery query, final SearchResult searchResult )
    {
        final NodeVersions.Builder entityVersionsBuilder = NodeVersions.create( query.getNodeId() );

        for ( final SearchHit searchHit : searchResult.getResults() )
        {
            entityVersionsBuilder.add( createVersionEntry( searchHit ) );
        }

        return entityVersionsBuilder.build();
    }

    private static NodeVersion createVersionEntry( final SearchHit hit )
    {
        final String timestamp = getStringValue( hit, VersionIndexPath.TIMESTAMP, true );

        final String versionId = getStringValue( hit, VersionIndexPath.VERSION_ID, true );

        return NodeVersion.create().
            nodeVersionId( NodeVersionId.from( versionId ) ).
            timestamp( Strings.isNullOrEmpty( timestamp ) ? null : Instant.parse( timestamp ) ).
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
