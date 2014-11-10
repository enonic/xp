package com.enonic.wem.core.elasticsearch;

import java.time.Instant;
import java.util.Iterator;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.core.TestContext;
import com.enonic.wem.core.entity.FindNodeVersionsResult;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeVersion;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntries;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.version.GetVersionsQuery;

import static com.enonic.wem.core.elasticsearch.xcontent.VersionXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.xcontent.VersionXContentBuilderFactory.TIMESTAMP_ID_FIELD_NAME;
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
            nodeId( NodeId.from( "1" ) ).
            build();

        // Newest first
        final Instant first = Instant.parse( "2014-07-18T09:17:42.855Z" );
        final Instant second = Instant.parse( "2013-07-18T09:17:42.855Z" );
        final Instant third = Instant.parse( "2012-07-18T09:17:42.855Z" );

        Mockito.when( elasticsearchDao.find( Mockito.any( QueryProperties.class ), Mockito.isA( TermQueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        id( "1" ).
                        score( 5 ).
                        addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, "a" ) ).
                        addField( TIMESTAMP_ID_FIELD_NAME, new SearchResultField( TIMESTAMP_ID_FIELD_NAME, first ) ).
                        build() ).
                    add( SearchResultEntry.create().
                        id( "2" ).
                        score( 4 ).
                        addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, "c" ) ).
                        addField( TIMESTAMP_ID_FIELD_NAME, new SearchResultField( TIMESTAMP_ID_FIELD_NAME, third ) ).
                        build() ).
                    add( SearchResultEntry.create().
                        id( "3" ).
                        score( 3 ).
                        addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, "b" ) ).
                        addField( TIMESTAMP_ID_FIELD_NAME, new SearchResultField( TIMESTAMP_ID_FIELD_NAME, second ) ).
                        build() ).
                    build() ).
                build() );

        final FindNodeVersionsResult result = service.findVersions( query, TestContext.TEST_REPOSITORY.getId() );

        assertEquals( 3, result.getNodeVersions().size() );

        final Iterator<NodeVersion> iterator = result.getNodeVersions().iterator();
        assertEquals( new BlobKey( "a" ), new BlobKey( iterator.next().getId().toString() ) );
        assertEquals( new BlobKey( "b" ), new BlobKey( iterator.next().getId().toString() ) );
        assertEquals( new BlobKey( "c" ), new BlobKey( iterator.next().getId().toString() ) );
    }
}