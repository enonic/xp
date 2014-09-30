package com.enonic.wem.core.elasticsearch;

import java.time.Instant;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.entity.EntityId;
import com.enonic.wem.core.entity.FindNodeVersionsResult;
import com.enonic.wem.core.entity.NodeVersion;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersions;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.core.version.EntityVersionDocument;
import com.enonic.wem.core.version.GetVersionsQuery;
import com.enonic.wem.core.version.VersionService;

import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.ENTITY_ID_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.TIMESTAMP_ID_FIELD_NAME;

public class ElasticsearchVersionService
    implements VersionService
{
    private static final boolean DEFAULT_REFRESH = true;

    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final EntityVersionDocument entityVersionDocument, final Repository repository )
    {
        final IndexRequest versionsDocument = Requests.indexRequest().
            index( StorageNameResolver.resolveStorageIndexName( repository ) ).
            type( IndexType.VERSION.getName() ).
            source( VersionXContentBuilderFactory.create( entityVersionDocument ) ).
            id( entityVersionDocument.getNodeVersionId().toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( versionsDocument );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionId nodeVersionId, final Repository repository )
    {
        final SearchResult searchResult = doGetFromVersionId( nodeVersionId, repository );

        final SearchResultEntry searchHit = searchResult.getResults().getFirstHit();

        return createVersionEntry( searchHit );
    }

    private NodeVersion createVersionEntry( final SearchResultEntry hit )
    {
        final String timestamp = getStringValue( hit, TIMESTAMP_ID_FIELD_NAME, true );
        final String versionId = getStringValue( hit, NODE_VERSION_ID_FIELD_NAME, true );

        return new NodeVersion( NodeVersionId.from( versionId ), Instant.parse( timestamp ) );
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final Repository repository )
    {
        final SearchResult searchResults = doGetFromEntityId( query.getEntityId(), query.getFrom(), query.getSize(), repository );

        final FindNodeVersionsResult.Builder findEntityVersionResultBuilder = FindNodeVersionsResult.create();

        findEntityVersionResultBuilder.hits( searchResults.getResults().getSize() );
        findEntityVersionResultBuilder.totalHits( searchResults.getResults().getTotalHits() );
        findEntityVersionResultBuilder.from( query.getFrom() );
        findEntityVersionResultBuilder.to( query.getSize() );

        final NodeVersions nodeVersions = buildEntityVersions( query, searchResults );

        findEntityVersionResultBuilder.entityVersions( nodeVersions );

        return findEntityVersionResultBuilder.build();
    }

    private NodeVersions buildEntityVersions( final GetVersionsQuery query, final SearchResult searchResults )
    {
        final NodeVersions.Builder entityVersionsBuilder = NodeVersions.create( query.getEntityId() );

        for ( final SearchResultEntry searchResult : searchResults.getResults() )
        {
            entityVersionsBuilder.add( createVersionEntry( searchResult ) );
        }

        return entityVersionsBuilder.build();
    }

    private SearchResult doGetFromEntityId( final EntityId id, final int from, final int size, final Repository repository )
    {
        final TermQueryBuilder entityIdQuery = new TermQueryBuilder( ENTITY_ID_FIELD_NAME, id.toString() );

        final QueryMetaData queryMetaData =
            createQueryMetaData( from, size, repository, TIMESTAMP_ID_FIELD_NAME, NODE_VERSION_ID_FIELD_NAME );

        final SearchResult searchResults = elasticsearchDao.get( queryMetaData, entityIdQuery );

        if ( searchResults.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with entityId: " + id );
        }
        return searchResults;
    }

    private SearchResult doGetFromVersionId( final NodeVersionId nodeVersionId, final Repository repository )
    {
        final TermQueryBuilder blobKeyQuery = new TermQueryBuilder( NODE_VERSION_ID_FIELD_NAME, nodeVersionId.toString() );

        final QueryMetaData queryMetaData = createQueryMetaData( 0, 1, repository, NODE_VERSION_ID_FIELD_NAME, TIMESTAMP_ID_FIELD_NAME );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, blobKeyQuery );

        if ( searchResult.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with blobKey: " + nodeVersionId );
        }
        return searchResult;
    }

    private QueryMetaData createQueryMetaData( final int from, final int size, final Repository repository, final String... fieldNames )
    {

        final SortBuilder descendingTimestampSort = new FieldSortBuilder( TIMESTAMP_ID_FIELD_NAME ).order( SortOrder.DESC );

        // TODO: Temp fix
        return QueryMetaData.create( StorageNameResolver.resolveStorageIndexName( repository ) ).
            indexTypeName( IndexType.VERSION.getName() ).
            addFields( fieldNames ).
            size( size ).
            from( from ).
            addSort( descendingTimestampSort ).
            build();
    }

    private String getStringValue( final SearchResultEntry hit, final String fieldName, final boolean required )
    {
        final SearchResultField field = hit.getField( fieldName, required );

        if ( field == null )
        {
            return null;
        }

        return field.getValue().toString();
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
