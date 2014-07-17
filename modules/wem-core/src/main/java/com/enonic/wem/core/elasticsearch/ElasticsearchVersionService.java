package com.enonic.wem.core.elasticsearch;

import java.time.Instant;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.google.inject.Inject;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.version.VersionDocument;
import com.enonic.wem.core.version.VersionEntry;
import com.enonic.wem.core.version.VersionService;

import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.BLOBKEY_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.TIMESTAMP_ID_FIELD_NAME;

public class ElasticsearchVersionService
    implements VersionService
{
    private final static Index VERSION_INDEX = Index.VERSION;

    private static final boolean DEFAULT_REFRESH = true;

    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final VersionDocument versionDocument )
    {
        final IndexRequest versionsDocument = Requests.indexRequest().
            index( VERSION_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            source( VersionXContentBuilderFactory.create( versionDocument ) ).
            id( versionDocument.getId().toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( versionsDocument );
    }

    @Override
    public VersionEntry getVersion( final BlobKey blobKey )
    {

        final TermQueryBuilder blobKeyQuery = new TermQueryBuilder( BLOBKEY_FIELD_NAME, blobKey.toString() );

        final QueryMetaData queryMetaData = QueryMetaData.create( VERSION_INDEX ).
            addFields( TIMESTAMP_ID_FIELD_NAME ).
            size( 1 ).
            from( 0 ).
            indexType( IndexType.NODE ).
            build();

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, blobKeyQuery );

        if ( searchResult.isEmpty() )
        {
            throw new RuntimeException( "Did not find version entry with blobKey: " + blobKey );
        }

        final SearchResultEntry firstHit = searchResult.getResults().getFirstHit();

        final String longValue = getStringValue( firstHit, TIMESTAMP_ID_FIELD_NAME, true );

        return new VersionEntry( blobKey, Instant.parse( longValue ) );
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
