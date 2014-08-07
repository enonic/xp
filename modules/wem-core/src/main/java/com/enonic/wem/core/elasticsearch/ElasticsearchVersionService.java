package com.enonic.wem.core.elasticsearch;

import java.time.Instant;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityVersion;
import com.enonic.wem.api.entity.EntityVersions;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.version.EntityVersionDocument;
import com.enonic.wem.core.version.GetVersionsQuery;
import com.enonic.wem.core.version.VersionService;

import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.BLOBKEY_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.ENTITY_ID_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.TIMESTAMP_ID_FIELD_NAME;

public class ElasticsearchVersionService
    implements VersionService
{
    private final static Index VERSION_INDEX = Index.VERSION;

    private static final boolean DEFAULT_REFRESH = true;

    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final EntityVersionDocument entityVersionDocument )
    {
        final IndexRequest versionsDocument = Requests.indexRequest().
            index( VERSION_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            source( VersionXContentBuilderFactory.create( entityVersionDocument ) ).
            id( entityVersionDocument.getId().toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( versionsDocument );
    }

    @Override
    public EntityVersion getVersion( final BlobKey blobKey )
    {
        final SearchResult searchResult = doGetFromBlobKey( blobKey );

        final SearchResultEntry searchHit = searchResult.getResults().getFirstHit();

        return createVersionEntry( searchHit );
    }

    private EntityVersion createVersionEntry( final SearchResultEntry hit )
    {
        final String timestamp = getStringValue( hit, TIMESTAMP_ID_FIELD_NAME, true );
        final String blobKey = getStringValue( hit, BLOBKEY_FIELD_NAME, true );

        return new EntityVersion( new BlobKey( blobKey ), Instant.parse( timestamp ) );
    }

    @Override
    public EntityVersions getVersions( final GetVersionsQuery query )
    {
        final SearchResult searchResults = doGetFromEntityId( query.getEntityId(), query.getFrom(), query.getSize() );

        final EntityVersions.Builder builder = EntityVersions.create( query.getEntityId() );

        for ( final SearchResultEntry searchResult : searchResults.getResults() )
        {
            builder.add( createVersionEntry( searchResult ) );
        }

        return builder.build();
    }

    private SearchResult doGetFromEntityId( final EntityId id, final int from, final int size )
    {
        final TermQueryBuilder entityIdQuery = new TermQueryBuilder( ENTITY_ID_FIELD_NAME, id.toString() );

        final QueryMetaData queryMetaData = createQueryMetaData( from, size, TIMESTAMP_ID_FIELD_NAME, BLOBKEY_FIELD_NAME );

        final SearchResult searchResults = elasticsearchDao.get( queryMetaData, entityIdQuery );

        if ( searchResults.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with entityId: " + id );
        }
        return searchResults;
    }

    private SearchResult doGetFromBlobKey( final BlobKey blobKey )
    {
        final TermQueryBuilder blobKeyQuery = new TermQueryBuilder( BLOBKEY_FIELD_NAME, blobKey.toString() );

        final QueryMetaData queryMetaData = createQueryMetaData( 0, 1, BLOBKEY_FIELD_NAME, TIMESTAMP_ID_FIELD_NAME );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, blobKeyQuery );

        if ( searchResult.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with blobKey: " + blobKey );
        }
        return searchResult;
    }

    private QueryMetaData createQueryMetaData( final int from, final int size, final String... fieldNames )
    {

        SortBuilder descendingTimestampSort = new FieldSortBuilder( TIMESTAMP_ID_FIELD_NAME ).order( SortOrder.DESC );

        return QueryMetaData.create( VERSION_INDEX ).
            addFields( fieldNames ).
            size( size ).
            from( from ).
            indexType( IndexType.NODE ).
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

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
