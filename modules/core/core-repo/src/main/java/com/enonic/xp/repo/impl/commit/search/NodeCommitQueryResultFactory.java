package com.enonic.xp.repo.impl.commit.search;

import java.time.Instant;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeCommitEntries;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeCommitQueryResult;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.commit.storage.CommitIndexPath;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.security.PrincipalKey;

public class NodeCommitQueryResultFactory
{
    public static NodeCommitQueryResult create( final SearchResult searchResult )
    {
        if ( searchResult.isEmpty() )
        {
            final long totalHits = searchResult.getTotalHits();
            return NodeCommitQueryResult.create().
                nodeCommitEntries( NodeCommitEntries.empty() ).
                totalHits( totalHits ).
                build();
        }

        final NodeCommitQueryResult.Builder nodeCommitQueryResult = NodeCommitQueryResult.create();

        nodeCommitQueryResult.totalHits( searchResult.getTotalHits() );

        nodeCommitQueryResult.nodeCommitEntries( buildNodeCommitEntries( searchResult ) );

        return nodeCommitQueryResult.build();
    }

    private static NodeCommitEntries buildNodeCommitEntries( final SearchResult searchResult )
    {
        final NodeCommitEntries.Builder nodeCommitEntries = NodeCommitEntries.create();

        for ( final SearchHit searchHit : searchResult.getHits() )
        {
            nodeCommitEntries.add( createNodeCommitEntry( searchHit ) );
        }

        return nodeCommitEntries.build();
    }

    private static NodeCommitEntry createNodeCommitEntry( final SearchHit hit )
    {
        final String commitId = getStringValue( hit, CommitIndexPath.COMMIT_ID, true );

        final String message = getStringValue( hit, CommitIndexPath.MESSAGE, false );

        final String committer = getStringValue( hit, CommitIndexPath.COMMITTER, false );

        final String timestamp = getStringValue( hit, CommitIndexPath.TIMESTAMP, true );

        return NodeCommitEntry.create().
            nodeCommitId( NodeCommitId.from( commitId ) ).
            message( message ).
            committer( PrincipalKey.from(committer) ).
            timestamp( Instant.parse( timestamp ) ).
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
