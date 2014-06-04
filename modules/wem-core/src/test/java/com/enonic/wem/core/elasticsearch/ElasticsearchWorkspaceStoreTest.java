package com.enonic.wem.core.elasticsearch;

import java.util.Arrays;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntries;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

import static com.enonic.wem.core.elasticsearch.WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME;

public class ElasticsearchWorkspaceStoreTest
{

    private ElasticsearchDao elasticsearchDao;

    private ElasticsearchWorkspaceStore wsStore;


    @Before
    public void setUp()
        throws Exception
    {
        elasticsearchDao = Mockito.mock( ElasticsearchDao.class );
        this.wsStore = new ElasticsearchWorkspaceStore();
        this.wsStore.setElasticsearchDao( elasticsearchDao );
    }

    @Test
    public void getById()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "a" ).
                    addField( BLOBKEY_FIELD_NAME, new SearchResultField( BLOBKEY_FIELD_NAME, Arrays.asList( "myBlobKey" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).thenReturn(
            searchResult );

        final WorkspaceIdQuery idQuery = new WorkspaceIdQuery( new Workspace( "test" ), EntityId.from( "1" ) );

        final BlobKey blobKey = wsStore.getById( idQuery );

        Assert.assertEquals( new BlobKey( "myBlobKey" ), blobKey );
    }


    @Test(expected = NodeNotFoundException.class)
    public void getById_no_hits()
        throws Exception
    {
        final SearchResult emptySearchResult = SearchResult.create().
            results( SearchResultEntries.create().
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).thenReturn(
            emptySearchResult );

        final WorkspaceIdQuery idQuery = new WorkspaceIdQuery( new Workspace( "test" ), EntityId.from( "1" ) );

        wsStore.getById( idQuery );
    }

    @Test(expected = WorkspaceStoreException.class)
    public void getById_missing_field()
        throws Exception
    {
        final SearchResult searchResultMissingField = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "a" ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).thenReturn(
            searchResultMissingField );

        final WorkspaceIdQuery idQuery = new WorkspaceIdQuery( new Workspace( "test" ), EntityId.from( "1" ) );

        wsStore.getById( idQuery );
    }

}
