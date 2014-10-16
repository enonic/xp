package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Arrays;
import java.util.Iterator;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Iterators;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.TestContext;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.ElasticsearchDataException;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodePaths;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntries;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceContext;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;

import static com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME;
import static org.junit.Assert.*;

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

        final SearchResult notExisting = SearchResult.create().results( SearchResultEntries.create().build() ).build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( String.class ) ) ).
            thenReturn( notExisting );

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

        final SearchResult noChanges = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "1" ).
                    score( 1 ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                build() ).
            build();

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

        final SearchResult noChanges = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "1" ).
                    score( 1 ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( String.class ) ) ).
            thenReturn( noChanges );

        wsStore.store( storeWorkspaceDocument, WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        Mockito.verify( elasticsearchDao, Mockito.times( 1 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void delete()
    {
        wsStore.delete( NodeId.from( "1" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );
    }

    @Test
    public void getById()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "a" ).
                    addField( NODE_VERSION_ID_FIELD_NAME,
                              new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "versionId" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionId version = wsStore.getCurrentVersion( NodeId.from( "1" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        Assert.assertEquals( NodeVersionId.from( "versionId" ), version );
    }


    @Test
    public void getById_no_hits()
        throws Exception
    {
        final SearchResult emptySearchResult = SearchResult.create().
            results( SearchResultEntries.create().
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( emptySearchResult );

        final NodeVersionId version = wsStore.getCurrentVersion( NodeId.from( "1" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertTrue( version == null );
    }

    @Test(expected = ElasticsearchDataException.class)
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

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResultMissingField );

        wsStore.getCurrentVersion( NodeId.from( "1" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );
    }

    @Test
    public void getByIds()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "3" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions =
            wsStore.getByVersionIds( NodeIds.from( "1", "2", "3" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 3, Iterators.size( versions.iterator() ) );
        final Iterator<NodeVersionId> iterator = versions.iterator();
        NodeVersionId next = iterator.next();
        assertEquals( next, NodeVersionId.from( "b" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "a" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "c" ) );
    }

    @Test
    public void getByIds_preserveSorting()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "3" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions =
            wsStore.getByVersionIds( NodeIds.from( "3", "1", "2" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 3, Iterators.size( versions.iterator() ) );
        final Iterator<NodeVersionId> iterator = versions.iterator();
        NodeVersionId next = iterator.next();
        assertEquals( next, NodeVersionId.from( "c" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "a" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "b" ) );
    }


    @Test
    public void getByIds_missing_entry()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions =
            wsStore.getByVersionIds( NodeIds.from( "2", "3", "1", "4" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 2, versions.getSize() );
    }

    @Test
    public void getByIds_no_entries()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions =
            wsStore.getByVersionIds( NodeIds.from( "1", "2", "3" ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 0, versions.getSize() );
    }

    @Test
    public void getByPath()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "a" ).
                    addField( NODE_VERSION_ID_FIELD_NAME,
                              new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "myBlobKey" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionId version =
            wsStore.getByPath( NodePath.newPath( "/test" ).build(), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        Assert.assertEquals( NodeVersionId.from( "myBlobKey" ), version );
    }

    @Test
    public void getByPath_no_hits()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionId version =
            wsStore.getByPath( NodePath.newPath( "/test" ).build(), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertTrue( version == null );
    }

    @Test
    public void getByPaths()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "b" ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( "a" ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( "c" ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions = wsStore.getByPaths(
            NodePaths.from( NodePath.newPath( "/test" ).build(), NodePath.newPath( "/test2" ).build(),
                            NodePath.newPath( "/test3" ).build() ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 3, Iterators.size( versions.iterator() ) );
        final Iterator<NodeVersionId> iterator = versions.iterator();
        NodeVersionId next = iterator.next();
        assertEquals( next, NodeVersionId.from( "b" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "a" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "c" ) );
    }

    @Test
    public void getByPaths_missing()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( "b" ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( "a" ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions = wsStore.getByPaths(
            NodePaths.from( NodePath.newPath( "/test" ).build(), NodePath.newPath( "/test2" ).build(),
                            NodePath.newPath( "/test3" ).build() ), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );
        assertEquals( 2, versions.getSize() );
    }

    @Test
    public void getByParent()
        throws Exception
    {

        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( NodeId.from( "3" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final NodeVersionIds versions =
            wsStore.findByParent( NodePath.newPath( "/test" ).build(), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 3, Iterators.size( versions.iterator() ) );
        final Iterator<NodeVersionId> iterator = versions.iterator();
        NodeVersionId next = iterator.next();
        assertEquals( next, NodeVersionId.from( "b" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "a" ) );
        next = iterator.next();
        assertEquals( next, NodeVersionId.from( "c" ) );
    }

    @Test
    public void getByParent_empty()
        throws Exception
    {
        final SearchResult emptyResult = SearchResult.create().
            results( SearchResultEntries.create().
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( emptyResult );

        final NodeVersionIds versions =
            wsStore.findByParent( NodePath.newPath( "/test" ).build(), WorkspaceContext.from( TestContext.TEST_CONTEXT ) );

        assertEquals( 0, Iterators.size( versions.iterator() ) );
    }
}
