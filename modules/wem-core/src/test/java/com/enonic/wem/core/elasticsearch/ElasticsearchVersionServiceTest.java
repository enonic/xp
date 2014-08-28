package com.enonic.wem.core.elasticsearch;

import java.time.Instant;
import java.util.Iterator;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.FindNodeVersionsResult;
import com.enonic.wem.api.entity.NodeVersion;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntries;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.version.GetVersionsQuery;

import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.BLOBKEY_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.VersionXContentBuilderFactory.TIMESTAMP_ID_FIELD_NAME;
import static org.junit.Assert.*;

public class ElasticsearchVersionServiceTest
{

    private ElasticsearchVersionService service;

    private ElasticsearchDao elasticsearchDao;

    @Before
    public void setUp()
        throws Exception
    {
        service = new ElasticsearchVersionService();

        elasticsearchDao = Mockito.mock( ElasticsearchDao.class );

        service.setElasticsearchDao( elasticsearchDao );
    }

    @Test
    public void getVersions()
        throws Exception
    {
        final GetVersionsQuery query = GetVersionsQuery.create().
            entityId( EntityId.from( "1" ) ).
            build();

        // Newest first
        final Instant first = Instant.parse( "2014-07-18T09:17:42.855Z" );
        final Instant second = Instant.parse( "2013-07-18T09:17:42.855Z" );
        final Instant third = Instant.parse( "2012-07-18T09:17:42.855Z" );

        Mockito.when( elasticsearchDao.get( Mockito.any( QueryMetaData.class ), Mockito.isA( TermQueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        id( "1" ).
                        score( 5 ).
                        addField( BLOBKEY_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "a" ) ).
                        addField( TIMESTAMP_ID_FIELD_NAME, new SearchResultField( TIMESTAMP_ID_FIELD_NAME, first ) ).
                        build() ).
                    add( SearchResultEntry.create().
                        id( "2" ).
                        score( 4 ).
                        addField( BLOBKEY_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "c" ) ).
                        addField( TIMESTAMP_ID_FIELD_NAME, new SearchResultField( TIMESTAMP_ID_FIELD_NAME, third ) ).
                        build() ).
                    add( SearchResultEntry.create().
                        id( "3" ).
                        score( 3 ).
                        addField( BLOBKEY_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, "b" ) ).
                        addField( TIMESTAMP_ID_FIELD_NAME, new SearchResultField( TIMESTAMP_ID_FIELD_NAME, second ) ).
                        build() ).
                    build() ).
                build() );

        final FindNodeVersionsResult result = service.findVersions( query );

        assertEquals( 3, result.getNodeVersions().size() );

        final Iterator<NodeVersion> iterator = result.getNodeVersions().iterator();
        assertEquals( new BlobKey( "a" ), iterator.next().getBlobKey() );
        assertEquals( new BlobKey( "b" ), iterator.next().getBlobKey() );
        assertEquals( new BlobKey( "c" ), iterator.next().getBlobKey() );
    }
}