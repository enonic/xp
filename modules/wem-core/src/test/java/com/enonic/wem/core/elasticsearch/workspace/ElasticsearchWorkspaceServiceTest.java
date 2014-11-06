package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Arrays;

import org.elasticsearch.action.index.IndexRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.core.TestContext;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.index.result.GetResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;

import static com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME;

public class ElasticsearchWorkspaceServiceTest
{
    private ElasticsearchDao elasticsearchDao;

    private ElasticsearchWorkspaceService wsStore;

    @Before
    public void setUp()
        throws Exception
    {
        elasticsearchDao = Mockito.mock( ElasticsearchDao.class );
        this.wsStore = new ElasticsearchWorkspaceService();
        this.wsStore.setElasticsearchDao( elasticsearchDao );
    }

    @Test
    public void store_new()
    {
        final Node node = Node.newNode().
            id( NodeId.from( "1" ) ).
            name( NodeName.from( "mynode" ) ).
            build();

        final StoreWorkspaceDocument storeWorkspaceDocument = StoreWorkspaceDocument.create().
            nodeVersionId( NodeVersionId.from( "a" ) ).
            parentPath( node.parent() ).
            path( node.path() ).
            id( node.id() ).
            build();

        final GetResult empty = GetResult.empty();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( String.class ) ) ).
            thenReturn( empty );

        wsStore.store( storeWorkspaceDocument, WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        Mockito.verify( elasticsearchDao, Mockito.times( 1 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void store_no_changes()
    {
        final Node node = Node.newNode().
            id( NodeId.from( "1" ) ).
            name( NodeName.from( "mynode" ) ).
            build();

        final StoreWorkspaceDocument storeWorkspaceDocument = StoreWorkspaceDocument.create().
            nodeVersionId( NodeVersionId.from( "a" ) ).
            parentPath( node.parent() ).
            path( node.path() ).
            id( node.id() ).
            build();

        final GetResult noChanges = new GetResult( SearchResultEntry.create().
            id( "1" ).
            score( 1 ).
            addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
            build() );

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( String.class ) ) ).
            thenReturn( noChanges );

        wsStore.store( storeWorkspaceDocument, WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

//        Mockito.verify( elasticsearchDao, Mockito.times( 0 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void store_has_changes()
    {
        final Node node = Node.newNode().
            id( NodeId.from( "1" ) ).
            name( NodeName.from( "mynode" ) ).
            build();

        final StoreWorkspaceDocument storeWorkspaceDocument = StoreWorkspaceDocument.create().
            nodeVersionId( NodeVersionId.from( "a" ) ).
            parentPath( node.parent() ).
            path( node.path() ).
            id( node.id() ).
            build();

        final GetResult hasChanges = new GetResult( SearchResultEntry.create().
            id( "1" ).
            score( 1 ).
            addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
            build() );

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( String.class ) ) ).
            thenReturn( hasChanges );

        wsStore.store( storeWorkspaceDocument, WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        Mockito.verify( elasticsearchDao, Mockito.times( 1 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void delete()
    {
        wsStore.delete( NodeId.from( "1" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );
    }

}
