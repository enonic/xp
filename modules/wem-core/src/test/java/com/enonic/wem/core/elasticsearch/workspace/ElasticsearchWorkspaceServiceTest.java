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

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.Workspaces;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.ElasticsearchDataException;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntries;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;
import com.enonic.wem.core.elasticsearch.result.SearchResultField;
import com.enonic.wem.core.workspace.StoreWorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

import static com.enonic.wem.core.elasticsearch.workspace.WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME;
import static org.junit.Assert.*;

public class ElasticsearchWorkspaceServiceTest
{
    private ElasticsearchDao elasticsearchDao;

    private ElasticsearchWorkspaceService wsStore;

    private Workspace testWorkspace = Workspace.from( "test" );

    private Repository testRepo = Repository.create().
        workspaces( Workspaces.from( testWorkspace ) ).
        id( RepositoryId.from( "test" ) ).
        build();

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
            id( EntityId.from( "1" ) ).
            name( NodeName.from( "mynode" ) ).
            build();

        final StoreWorkspaceDocument storeWorkspaceDocument = StoreWorkspaceDocument.create().
            nodeVersionId( NodeVersionId.from( "a" ) ).
            workspace( Workspace.from( "test" ) ).
            parentPath( node.parent() ).
            path( node.path() ).
            id( node.id() ).
            repository( testRepo ).
            build();

        final SearchResult notExisting = SearchResult.create().results( SearchResultEntries.create().build() ).build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( String.class ) ) ).
            thenReturn( notExisting );

        wsStore.store( storeWorkspaceDocument );

        Mockito.verify( elasticsearchDao, Mockito.times( 1 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void store_no_changes()
    {
        final Node node = Node.newNode().
            id( EntityId.from( "1" ) ).
            name( NodeName.from( "mynode" ) ).
            build();

        final StoreWorkspaceDocument storeWorkspaceDocument = StoreWorkspaceDocument.create().
            nodeVersionId( NodeVersionId.from( "a" ) ).
            workspace( Workspace.from( "test" ) ).
            parentPath( node.parent() ).
            path( node.path() ).
            id( node.id() ).
            repository( testRepo ).
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

        wsStore.store( storeWorkspaceDocument );

//        Mockito.verify( elasticsearchDao, Mockito.times( 0 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void store_has_changes()
    {
        final Node node = Node.newNode().
            id( EntityId.from( "1" ) ).
            name( NodeName.from( "mynode" ) ).
            build();

        final StoreWorkspaceDocument storeWorkspaceDocument = StoreWorkspaceDocument.create().
            nodeVersionId( NodeVersionId.from( "a" ) ).
            workspace( Workspace.from( "test" ) ).
            parentPath( node.parent() ).
            path( node.path() ).
            id( node.id() ).
            repository( testRepo ).
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

        wsStore.store( storeWorkspaceDocument );

        Mockito.verify( elasticsearchDao, Mockito.times( 1 ) ).store( Mockito.isA( IndexRequest.class ) );
    }

    @Test
    public void delete()
    {
        final WorkspaceDeleteQuery deleteQuery = WorkspaceDeleteQuery.create().
            entityId( EntityId.from( "1" ) ).
            repository( testRepo ).
            workspace( testWorkspace ).
            build();

        wsStore.delete( deleteQuery );
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

        final WorkspaceIdQuery idQuery = WorkspaceIdQuery.create().
            entityId( EntityId.from( "1" ) ).
            repository( testRepo ).
            workspace( testWorkspace ).
            build();

        final NodeVersionId version = wsStore.getCurrentVersion( idQuery );

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

        final WorkspaceIdQuery idQuery = WorkspaceIdQuery.create().
            entityId( EntityId.from( "1" ) ).
            repository( testRepo ).
            workspace( testWorkspace ).
            build();

        final NodeVersionId version = wsStore.getCurrentVersion( idQuery );

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

        final WorkspaceIdQuery idQuery = WorkspaceIdQuery.create().
            entityId( EntityId.from( "1" ) ).
            repository( testRepo ).
            workspace( testWorkspace ).
            build();

        wsStore.getCurrentVersion( idQuery );
    }


    @Test
    public void getByIds()
        throws Exception
    {
        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "3" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final WorkspaceIdsQuery idQuery = WorkspaceIdsQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            entityIds( EntityIds.from( "1", "2", "3" ) ).
            build();

        final NodeVersionIds versions = wsStore.getByVersionIds( idQuery );

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
                    id( new WorkspaceDocumentId( EntityId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "3" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final WorkspaceIdsQuery idQuery = WorkspaceIdsQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            entityIds( EntityIds.from( "3", "1", "2" ) ).
            build();

        final NodeVersionIds versions = wsStore.getByVersionIds( idQuery );

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
                    id( new WorkspaceDocumentId( EntityId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final WorkspaceIdsQuery idQuery = WorkspaceIdsQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            entityIds( EntityIds.from( "2", "3", "1", "4" ) ).
            build();

        final NodeVersionIds versions = wsStore.getByVersionIds( idQuery );

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

        final WorkspaceIdsQuery idQuery = WorkspaceIdsQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            entityIds( EntityIds.from( "1", "2", "3" ) ).
            build();

        final NodeVersionIds versions = wsStore.getByVersionIds( idQuery );

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

        final WorkspacePathQuery pathQuery = WorkspacePathQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            nodePath( NodePath.newPath( "/test" ).build() ).
            build();

        final NodeVersionId version = wsStore.getByPath( pathQuery );

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

        final WorkspacePathQuery pathQuery = WorkspacePathQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            nodePath( NodePath.newPath( "/test" ).build() ).
            build();

        final NodeVersionId version = wsStore.getByPath( pathQuery );

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

        final WorkspacePathsQuery pathsQuery = WorkspacePathsQuery.create().
            workspace( Workspace.from( "test" ) ).
            repository( testRepo ).
            nodePaths( NodePaths.from( NodePath.newPath( "/test" ).build(), NodePath.newPath( "/test2" ).build(),
                                       NodePath.newPath( "/test3" ).build() ) ).
            build();

        final NodeVersionIds versions = wsStore.getByPaths( pathsQuery );

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

        final WorkspacePathsQuery pathsQuery = WorkspacePathsQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            nodePaths( NodePaths.from( NodePath.newPath( "/test" ).build(), NodePath.newPath( "/test2" ).build(),
                                       NodePath.newPath( "/test3" ).build() ) ).
            build();

        final NodeVersionIds versions = wsStore.getByPaths( pathsQuery );
        assertEquals( 2, versions.getSize() );
    }

    @Test
    public void getByParent()
        throws Exception
    {

        final SearchResult searchResult = SearchResult.create().
            results( SearchResultEntries.create().
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "1" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "b" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "2" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "a" ) ) ).
                    build() ).
                add( SearchResultEntry.create().
                    id( new WorkspaceDocumentId( EntityId.from( "3" ), Workspace.from( "test" ) ).toString() ).
                    addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, Arrays.asList( "c" ) ) ).
                    build() ).
                build() ).
            build();

        Mockito.when( elasticsearchDao.get( Mockito.isA( QueryMetaData.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( searchResult );

        final WorkspaceParentQuery query = WorkspaceParentQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            parentPath( NodePath.newPath( "/test" ).build() ).
            build();

        final NodeVersionIds versions = wsStore.findByParent( query );

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

        final WorkspaceParentQuery query = WorkspaceParentQuery.create().
            workspace( testWorkspace ).
            repository( testRepo ).
            parentPath( NodePath.newPath( "/test" ).build() ).
            build();

        final NodeVersionIds versions = wsStore.findByParent( query );

        assertEquals( 0, Iterators.size( versions.iterator() ) );
    }
}
