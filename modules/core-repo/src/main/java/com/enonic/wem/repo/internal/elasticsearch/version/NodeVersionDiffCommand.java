package com.enonic.wem.repo.internal.elasticsearch.version;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

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

        final BoolQueryBuilder sourceOnly = new BoolQueryBuilder().
            must( isInWorkspace( indexType, sourceWs ) ).
            mustNot( isInWorkspace( indexType, targetWs ) );

        final BoolQueryBuilder targetOnly = new BoolQueryBuilder().
            must( isInWorkspace( indexType, targetWs ) ).
            mustNot( isInWorkspace( indexType, sourceWs ) );

        final BoolQueryBuilder deletedSourceOnly = new BoolQueryBuilder().
            must( deletedInWorkspace( indexType, sourceWs ) ).
            mustNot( deletedInWorkspace( indexType, targetWs ) );

        final BoolQueryBuilder deletedTargetOnly = new BoolQueryBuilder().
            must( deletedInWorkspace( indexType, targetWs ) ).
            mustNot( deletedInWorkspace( indexType, sourceWs ) );

        final ElasticsearchQuery esQuery = ElasticsearchQuery.create().
            index( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( new BoolQueryBuilder().
                should( sourceOnly ).
                should( targetOnly ).
                should( deletedSourceOnly ).
                should( deletedTargetOnly ) ).
            setReturnFields( ReturnFields.from( VersionIndexPath.NODE_ID, VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP ) ).
            size( query.getSize() ).
            from( query.getFrom() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( esQuery );

        final NodeVersionDiffResult.Builder builder = NodeVersionDiffResult.create();

        for ( final SearchResultEntry entry : searchResult.getResults() )
        {
            builder.add( NodeId.from( entry.getField( VersionIndexPath.NODE_ID.toString() ).getValue().toString() ) );
        }

        return builder.build();
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
