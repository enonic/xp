package com.enonic.xp.repo.impl.version.search;

import java.time.Instant;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.node.NodeVersionsMetadata;
import com.enonic.xp.repo.impl.ReturnValue;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.version.VersionIndexPath;

import static com.google.common.base.Strings.isNullOrEmpty;

public class NodeVersionQueryResultFactory
{
    public static NodeVersionQueryResult create( final NodeVersionQuery query, final SearchResult searchResult )
    {
        final NodeVersionQueryResult.Builder findNodeVersionsResult = NodeVersionQueryResult.create();

        findNodeVersionsResult.hits( searchResult.getHits().getSize() );
        findNodeVersionsResult.totalHits( searchResult.getTotalHits() );
        findNodeVersionsResult.from( query.getFrom() );
        findNodeVersionsResult.to( query.getSize() );

        final NodeVersionsMetadata nodeVersionsMetadata = buildEntityVersions( query, searchResult );

        findNodeVersionsResult.entityVersions( nodeVersionsMetadata );

        return findNodeVersionsResult.build();
    }

    private static NodeVersionsMetadata buildEntityVersions( final NodeVersionQuery query, final SearchResult searchResult )
    {
        final NodeVersionsMetadata.Builder entityVersionsBuilder = NodeVersionsMetadata.create( query.getNodeId() );

        for ( final SearchHit searchHit : searchResult.getHits() )
        {
            entityVersionsBuilder.add( createVersionEntry( searchHit ) );
        }

        return entityVersionsBuilder.build();
    }

    private static NodeVersionMetadata createVersionEntry( final SearchHit hit )
    {
        final String timestamp = getStringValue( hit, VersionIndexPath.TIMESTAMP, true );

        final String versionId = getStringValue( hit, VersionIndexPath.VERSION_ID, true );

        final String nodeBlobKey = getStringValue( hit, VersionIndexPath.NODE_BLOB_KEY, true );

        final String indexConfigBlobKey = getStringValue( hit, VersionIndexPath.INDEX_CONFIG_BLOB_KEY, true );

        final String accessControlBlobKey = getStringValue( hit, VersionIndexPath.ACCESS_CONTROL_BLOB_KEY, true );

        final ReturnValue binaryBlobKeyReturnValue = hit.getField( VersionIndexPath.BINARY_BLOB_KEYS.getPath(), false );

        final String nodePath = getStringValue( hit, VersionIndexPath.NODE_PATH, true );

        final String nodeId = getStringValue( hit, VersionIndexPath.NODE_ID, true );

        final String commitId = getStringValue( hit, VersionIndexPath.COMMIT_ID, false );

        final NodeVersionKey nodeVersionKey = NodeVersionKey.from( nodeBlobKey, indexConfigBlobKey, accessControlBlobKey );
        final BlobKeys binaryBlobKeys = toBlobKeys( binaryBlobKeyReturnValue );

        return NodeVersionMetadata.create().
            nodeVersionId( NodeVersionId.from( versionId ) ).
            nodeVersionKey( nodeVersionKey ).
            binaryBlobKeys( binaryBlobKeys ).
            timestamp( isNullOrEmpty( timestamp ) ? null : Instant.parse( timestamp ) ).
            nodePath( new NodePath( nodePath ) ).
            nodeId( NodeId.from( nodeId ) ).
            nodeCommitId( isNullOrEmpty( commitId ) ? null : NodeCommitId.from( commitId ) ).
            build();
    }

    private static BlobKeys toBlobKeys( final ReturnValue returnValue )
    {
        return returnValue != null ? returnValue.getValues()
            .stream()
            .map( value -> BlobKey.from( value.toString() ) )
            .collect( BlobKeys.collector() ) : BlobKeys.empty();
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
