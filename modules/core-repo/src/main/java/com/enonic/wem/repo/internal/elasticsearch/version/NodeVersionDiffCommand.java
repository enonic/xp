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

        final BoolQueryBuilder source = new BoolQueryBuilder().
            must( isInWorkspace( indexType, this.query.getSource() ) ).
            mustNot( isInWorkspace( indexType, this.query.getTarget() ) );

        final BoolQueryBuilder target = new BoolQueryBuilder().
            must( isInWorkspace( indexType, this.query.getTarget() ) ).
            mustNot( isInWorkspace( indexType, this.query.getSource() ) );

        final ElasticsearchQuery esQuery = ElasticsearchQuery.create().
            index( StorageNameResolver.resolveStorageIndexName( this.repositoryId ) ).
            indexType( IndexType.VERSION.getName() ).
            query( new BoolQueryBuilder().
                should( source ).
                should( target ) ).
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
