package com.enonic.wem.repo.internal.elasticsearch.workspace;


import org.elasticsearch.index.query.QueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.GetQuery;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.index.query.NodeWorkspaceVersion;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.workspace.NodeWorkspaceState;
import com.enonic.wem.repo.internal.workspace.StoreWorkspaceDocument;
import com.enonic.wem.repo.internal.workspace.WorkspaceContext;
import com.enonic.wem.repo.internal.workspace.WorkspaceDocumentId;
import com.enonic.wem.repo.internal.workspace.WorkspaceService;

public class ElasticsearchWorkspaceService
    implements WorkspaceService
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final StoreWorkspaceDocument storeWorkspaceDocument, final WorkspaceContext context )
    {
        StoreWorkspaceDocumentCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            workspace( context.getWorkspace() ).
            repository( context.getRepositoryId() ).
            document( storeWorkspaceDocument ).
            build().
            execute();
    }

    @Override
    public void delete( final NodeId nodeId, final WorkspaceContext context )
    {
        DeleteNodeVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( context.getRepositoryId() ).
            workspace( context.getWorkspace() ).
            nodeId( nodeId ).
            build().
            execute();
    }

    @Override
    public NodeWorkspaceVersion get( final NodeId nodeId, final WorkspaceContext context )
    {
        final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( nodeId, context.getWorkspace() );

        final GetResult getResult = this.elasticsearchDao.get( GetQuery.create().
            id( workspaceDocumentId.toString() ).
            indexName( StorageNameResolver.resolveStorageIndexName( ContextAccessor.current().getRepositoryId() ) ).
            indexTypeName( IndexType.WORKSPACE.getName() ).
            returnFields( ReturnFields.from( WorkspaceIndexPath.VERSION_ID, WorkspaceIndexPath.STATE, WorkspaceIndexPath.PATH ) ).
            routing( nodeId.toString() ).
            build() );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final NodeReturnValue nodeReturnValue = NodeReturnValue.from( getResult );

        return createFromReturnValue( nodeReturnValue );
    }

    @Override
    public NodeWorkspaceVersion get( final NodePath nodePath, final WorkspaceContext context )
    {
        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( ValueFilter.create().
                fieldName( WorkspaceIndexPath.PATH.getPath() ).
                addValue( Value.newString( nodePath.toString() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( StorageNameResolver.resolveStorageIndexName( ContextAccessor.current().getRepositoryId() ) ).
            indexType( IndexType.WORKSPACE.getName() ).
            query( queryBuilder ).
            size( 1 ).
            setReturnFields( ReturnFields.from( WorkspaceIndexPath.VERSION_ID, WorkspaceIndexPath.STATE, WorkspaceIndexPath.PATH ) ).
            build();

        final SearchResult searchResult = this.elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        final SearchResultEntry firstHit = searchResult.getResults().getFirstHit();

        final NodeReturnValue nodeReturnValue = NodeReturnValue.from( firstHit );

        return createFromReturnValue( nodeReturnValue );
    }

    private NodeWorkspaceVersion createFromReturnValue( final NodeReturnValue nodeReturnValue )
    {
        return NodeWorkspaceVersion.create().
            nodePath( nodeReturnValue.nodePath ).
            nodeVersionId( nodeReturnValue.getNodeVersionId() ).
            state( nodeReturnValue.getState() ).
            build();
    }

    private static class NodeReturnValue
    {
        private final NodePath nodePath;

        private final NodeVersionId nodeVersionId;

        private final NodeWorkspaceState state;

        public NodeReturnValue( final NodePath nodePath, final NodeVersionId nodeVersionId, final NodeWorkspaceState state )
        {
            this.nodePath = nodePath;
            this.nodeVersionId = nodeVersionId;
            this.state = state;
        }

        public static NodeReturnValue from( final SearchResultEntry searchResultEntry )
        {
            final SearchResultFieldValue nodeVersionIdValue = searchResultEntry.getField( WorkspaceIndexPath.VERSION_ID.getPath() );
            final SearchResultFieldValue nodePathValue = searchResultEntry.getField( WorkspaceIndexPath.PATH.getPath() );
            final SearchResultFieldValue stateValue = searchResultEntry.getField( WorkspaceIndexPath.STATE.getPath() );

            return createNodeReturnValue( nodeVersionIdValue, nodePathValue, stateValue );
        }

        public static NodeReturnValue from( final GetResult getResult )
        {
            final SearchResultFieldValue nodeVersionIdValue =
                getResult.getSearchResult().getField( WorkspaceIndexPath.VERSION_ID.getPath() );
            final SearchResultFieldValue nodePathValue = getResult.getSearchResult().getField( WorkspaceIndexPath.PATH.getPath() );
            final SearchResultFieldValue stateValue = getResult.getSearchResult().getField( WorkspaceIndexPath.STATE.getPath() );

            return createNodeReturnValue( nodeVersionIdValue, nodePathValue, stateValue );
        }

        private static NodeReturnValue createNodeReturnValue( final SearchResultFieldValue nodeVersionIdValue,
                                                              final SearchResultFieldValue nodePathValue,
                                                              final SearchResultFieldValue stateValue )
        {
            Preconditions.checkNotNull( nodeVersionIdValue,
                                        "Expected value '" + WorkspaceIndexPath.VERSION_ID.getPath() + "' in getResult " );
            Preconditions.checkNotNull( nodePathValue, "Expected value '" + WorkspaceIndexPath.PATH.getPath() + "' in getResult " );
            Preconditions.checkNotNull( stateValue, "Expected value '" + WorkspaceIndexPath.STATE.getPath() + "' in getResult " );

            return new NodeReturnValue( NodePath.newPath( nodePathValue.getValue().toString() ).build(),
                                        NodeVersionId.from( nodeVersionIdValue.getValue().toString() ),
                                        NodeWorkspaceState.from( stateValue.getValue().toString() ) );
        }

        public NodePath getNodePath()
        {
            return nodePath;
        }

        public NodeVersionId getNodeVersionId()
        {
            return nodeVersionId;
        }

        public NodeWorkspaceState getState()
        {
            return state;
        }
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}

