package com.enonic.xp.repo.impl.elasticsearch.branch;

import java.util.Collection;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.repo.impl.elasticsearch.ElasticsearchDao;
import com.enonic.xp.repo.impl.index.result.SearchResultFieldValue;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractBranchCommand
{
    static final boolean DEFAULT_REFRESH = true;

    final ElasticsearchDao elasticsearchDao;

    final RepositoryId repositoryId;

    AbstractBranchCommand( final Builder builder )
    {
        this.elasticsearchDao = builder.elasticsearchDao;
        this.repositoryId = builder.repositoryId;
    }

    NodeVersionIds fieldValuesToVersionIds( final Collection<SearchResultFieldValue> fieldValues )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        for ( final SearchResultFieldValue searchResultFieldValue : fieldValues )
        {
            if ( searchResultFieldValue == null )
            {
                continue;
            }

            builder.add( NodeVersionId.from( searchResultFieldValue.getValue().toString() ) );
        }
        return builder.build();
    }

    BoolQueryBuilder joinWithBranchQuery( final String branchName, final QueryBuilder specificQuery )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        final TermQueryBuilder branchQuery = new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.getPath(), branchName );
        boolQueryBuilder.must( specificQuery );
        boolQueryBuilder.must( branchQuery );

        return boolQueryBuilder;
    }

    static abstract class Builder<B extends Builder>
    {
        private ElasticsearchDao elasticsearchDao;

        private RepositoryId repositoryId;

        @SuppressWarnings("unchecked")
        B elasticsearchDao( final ElasticsearchDao elasticsearchDao )
        {
            this.elasticsearchDao = elasticsearchDao;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B repository( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return (B) this;
        }

    }

}
