package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.Objects;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class DiffQueryFactory
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private final NodePaths excludes;

    private DiffQueryFactory( Builder builder )
    {
        this.source = builder.query.getSource();
        this.target = builder.query.getTarget();
        this.nodePath = builder.query.getNodePath();
        this.excludes = builder.query.getExcludes();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public QueryBuilder execute()
    {
        return createDiffQuery();
    }

    private QueryBuilder createDiffQuery()
    {
        final BoolQueryBuilder inSourceOnly = onlyInQuery( this.source, this.target );
        final BoolQueryBuilder inTargetOnly = onlyInQuery( this.target, this.source );

        final BoolQueryBuilder sourceTargetCompares = QueryBuilders.boolQuery().should( inSourceOnly ).should( inTargetOnly );

        return wrapInPathQueryIfNecessary( sourceTargetCompares );
    }

    private BoolQueryBuilder onlyInQuery( final Branch source, final Branch target )
    {
        return QueryBuilders.boolQuery().must( isInBranch( source ) ).mustNot( isInBranch( target ) );
    }

    private QueryBuilder wrapInPathQueryIfNecessary( final BoolQueryBuilder sourceTargetCompares )
    {
        final BoolQueryBuilder result = QueryBuilders.boolQuery().filter( sourceTargetCompares );

        if ( this.nodePath != null && !this.nodePath.isRoot() )
        {
            final String queryPath = nodePath.toString().toLowerCase();

            final QueryBuilder pathQuery = QueryBuilders.boolQuery()
                .should( QueryBuilders.termQuery( BranchIndexPath.PATH.getPath(), queryPath ) )
                .should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(), queryPath + "/*" ) );
            result.filter( new HasChildQueryBuilder( StaticStorageType.BRANCH.getName(), pathQuery ) );
        }

        if ( !this.excludes.isEmpty() )
        {
            final QueryBuilder pathQuery = QueryBuilders.termsQuery( BranchIndexPath.PATH.getPath(), this.excludes.stream()
                    .map( excludeEntry -> excludeEntry.toString().toLowerCase() )
                    .toList() );
            result.mustNot( new HasChildQueryBuilder( StaticStorageType.BRANCH.getName(), pathQuery ) );
        }

        return result;
    }

    private HasChildQueryBuilder isInBranch( final Branch source )
    {
        return new HasChildQueryBuilder( StaticStorageType.BRANCH.getName(),
                                         QueryBuilders.termQuery( BranchIndexPath.BRANCH_NAME.getPath(), source.getValue() ) );
    }

    public static final class Builder
    {
        private NodeVersionDiffQuery query;

        private Builder()
        {
        }

        public Builder query( NodeVersionDiffQuery query )
        {
            this.query = query;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( this.query, "query is required" );
        }

        public DiffQueryFactory build()
        {
            this.validate();
            return new DiffQueryFactory( this );
        }
    }
}
