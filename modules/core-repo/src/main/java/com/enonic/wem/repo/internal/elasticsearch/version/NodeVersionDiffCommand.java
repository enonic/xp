package com.enonic.wem.repo.internal.elasticsearch.version;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.workspace.WorkspaceIndexPath;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.wem.repo.internal.workspace.NodeWorkspaceState;

class NodeVersionDiffCommand
    extends AbstractVersionsCommand
{
    private final NodeVersionDiffQuery query;

    private NodeVersionDiffCommand( final Builder builder )
    {
        super( builder );
        query = builder.query;
    }

    NodeVersionDiffResult execute()
    {
        final String indexType = IndexType.WORKSPACE.getName();

        final Workspace sourceWs = this.query.getSource();
        final Workspace targetWs = this.query.getTarget();

        final BoolQueryBuilder inSourceOnly = onlyInQuery( indexType, sourceWs, targetWs );

        final BoolQueryBuilder inTargetOnly = onlyInQuery( indexType, targetWs, sourceWs );

        final BoolQueryBuilder deletedInSourceOnly = deletedOnlyQuery( indexType, sourceWs, targetWs );

        final BoolQueryBuilder deletedInTargetOnly = deletedOnlyQuery( indexType, targetWs, sourceWs );

        final BoolQueryBuilder sourceTargetCompares =
            joinOnlyInQueries( inSourceOnly, inTargetOnly, deletedInSourceOnly, deletedInTargetOnly );

        final BoolQueryBuilder query = wrapInPathQueryIfNecessary( indexType, sourceTargetCompares );

        final ElasticsearchQuery esQuery = ElasticsearchQuery.create().
            index( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( query ).
            setReturnFields( ReturnFields.from( VersionIndexPath.NODE_ID, VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP ) ).
            size( this.query.getSize() ).
            from( this.query.getFrom() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( esQuery );

        final NodeVersionDiffResult.Builder builder = NodeVersionDiffResult.create();

        for ( final SearchResultEntry entry : searchResult.getResults() )
        {
            builder.add( NodeId.from( entry.getField( VersionIndexPath.NODE_ID.toString() ).getValue().toString() ) );
        }

        return builder.build();
    }

    private BoolQueryBuilder deletedOnlyQuery( final String indexType, final Workspace sourceWs, final Workspace targetWs )
    {
        return new BoolQueryBuilder().
            must( deletedInWorkspace( indexType, sourceWs ) ).
            mustNot( deletedInWorkspace( indexType, targetWs ) );
    }

    private BoolQueryBuilder onlyInQuery( final String indexType, final Workspace sourceWs, final Workspace targetWs )
    {
        return new BoolQueryBuilder().
            must( isInWorkspace( indexType, sourceWs ) ).
            mustNot( isInWorkspace( indexType, targetWs ) );
    }

    private BoolQueryBuilder wrapInPathQueryIfNecessary( final String indexType, final BoolQueryBuilder sourceTargetCompares )
    {
        if ( this.query.getNodePath() != null )
        {
            return new BoolQueryBuilder().
                must( hasPath( indexType ) ).
                must( sourceTargetCompares );
        }
        return sourceTargetCompares;
    }

    private BoolQueryBuilder joinOnlyInQueries( final BoolQueryBuilder inSourceOnly, final BoolQueryBuilder inTargetOnly,
                                                final BoolQueryBuilder deletedInSourceOnly, final BoolQueryBuilder deletedInTargetOnly )
    {
        return new BoolQueryBuilder().
            should( inSourceOnly ).
            should( inTargetOnly ).
            should( deletedInSourceOnly ).
            should( deletedInTargetOnly );
    }

    private HasChildQueryBuilder hasPath( final String indexType )
    {
        return new HasChildQueryBuilder( indexType, new WildcardQueryBuilder( WorkspaceIndexPath.PATH.getPath(),
                                                                              this.query.getNodePath().toString() + "*" ) );
    }

    private HasChildQueryBuilder deletedInWorkspace( final String indexType, final Workspace sourceWs )
    {
        return new HasChildQueryBuilder( indexType, new BoolQueryBuilder().
            must( isDeleted() ).
            must( new TermQueryBuilder( WorkspaceIndexPath.WORKSPACE_ID.toString(), sourceWs.getName() ) ) );
    }

    private TermQueryBuilder isDeleted()
    {
        return new TermQueryBuilder( WorkspaceIndexPath.STATE.toString(), NodeWorkspaceState.DELETED.value() );
    }

    private HasChildQueryBuilder isInWorkspace( final String indexType, final Workspace source1 )
    {
        return new HasChildQueryBuilder( indexType, createWsConstraint( source1 ) );
    }

    private TermQueryBuilder createWsConstraint( final Workspace ws )
    {
        return new TermQueryBuilder( WorkspaceIndexPath.WORKSPACE_ID.toString(), ws );
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
        extends AbstractVersionsCommand.Builder<Builder>
    {
        private NodeVersionDiffQuery query;

        private Builder()
        {
        }

        Builder query( NodeVersionDiffQuery query )
        {
            this.query = query;
            return this;
        }

        NodeVersionDiffCommand build()
        {
            return new NodeVersionDiffCommand( this );
        }
    }
}
