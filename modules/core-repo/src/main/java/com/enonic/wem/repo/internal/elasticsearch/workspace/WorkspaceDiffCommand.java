package com.enonic.wem.repo.internal.elasticsearch.workspace;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.wem.api.index.IndexPath;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.WorkspaceDiffResult;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.ReturnFields;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;
import com.enonic.wem.repo.internal.workspace.compare.query.WorkspaceDiffQuery;

public class WorkspaceDiffCommand
{
    private final WorkspaceDiffQuery query;

    private final ElasticsearchDao elasticsearchDao;

    private final RepositoryId repositoryId;

    private WorkspaceDiffCommand( Builder builder )
    {
        query = builder.query;
        elasticsearchDao = builder.elasticsearchDao;
        repositoryId = builder.repositoryId;
    }

    public WorkspaceDiffResult execute()
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
            indexType( IndexType.NODE.getName() ).
            query( new BoolQueryBuilder().
                should( source ).
                should( target ) ).
            setReturnFields( ReturnFields.from( IndexPath.from( "nodeid" ) ) ).
            size( query.getSize() ).
            from( query.getFrom() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( esQuery );

        final WorkspaceDiffResult.Builder builder = WorkspaceDiffResult.create();

        for ( final SearchResultEntry entry : searchResult.getResults() )
        {
            builder.add( NodeId.from( entry.getField( "nodeid" ).getValue().toString() ) );
        }

        return builder.build();
    }

    private HasChildQueryBuilder isInWorkspace( final String indexType, final Workspace source1 )
    {
        return new HasChildQueryBuilder( indexType, createWsConstraint( source1 ) );
    }

    private TermQueryBuilder createWsConstraint( final Workspace ws )
    {
        return new TermQueryBuilder( "workspace", ws );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private WorkspaceDiffQuery query;

        private ElasticsearchDao elasticsearchDao;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder query( WorkspaceDiffQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder elasticsearchDao( ElasticsearchDao elasticsearchDao )
        {
            this.elasticsearchDao = elasticsearchDao;
            return this;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public WorkspaceDiffCommand build()
        {
            return new WorkspaceDiffCommand( this );
        }
    }
}
