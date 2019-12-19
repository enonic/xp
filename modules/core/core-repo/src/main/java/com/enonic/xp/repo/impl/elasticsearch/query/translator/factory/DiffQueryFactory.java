package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class DiffQueryFactory
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private final boolean deleted;

    private DiffQueryFactory( Builder builder )
    {
        source = builder.query.getSource();
        target = builder.query.getTarget();
        nodePath = builder.query.getNodePath();
        this.excludes = builder.query.getExcludes();
        this.deleted = builder.query.isDeleted();
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
        final BoolQueryBuilder inSourceOnly = onlyInQuery( this.source );

        final BoolQueryBuilder inTargetOnly = onlyInQuery( this.target );

        final BoolQueryBuilder deletedInSourceOnly = deletedOnlyQuery( this.source );

        final BoolQueryBuilder deletedInTargetOnly = deletedOnlyQuery( this.target );

        return deleted ? wrapInPathQueryIfNecessary( new BoolQueryBuilder().
            should( deletedInSourceOnly ).
            should( deletedInTargetOnly ) ) : wrapInPathQueryIfNecessary( new BoolQueryBuilder().
            should( inSourceOnly ).
            should( inTargetOnly ) );
    }

    private BoolQueryBuilder deletedOnlyQuery( final Branch source )
    {
        return new BoolQueryBuilder().
            must( deletedInBranch( source ) );
    }

    private BoolQueryBuilder onlyInQuery( final Branch source )
    {
        return new BoolQueryBuilder().
            must( isInBranch( source ) );
    }

    private BoolQueryBuilder wrapInPathQueryIfNecessary( final BoolQueryBuilder sourceTargetCompares )
    {

        final BoolQueryBuilder pathFilter = new BoolQueryBuilder();

        boolean addedPathFilter = false;

        if ( this.nodePath != null && !this.nodePath.isRoot() )
        {
            addedPathFilter = true;
            pathFilter.
                must( hasPath( this.nodePath, true ) );
        }

        if ( !this.excludes.isEmpty() )
        {
            addedPathFilter = true;
            for ( final ExcludeEntry exclude : excludes )
            {
                pathFilter.
                    mustNot( hasPath( exclude.getNodePath(), exclude.isRecursive() ) );
            }
        }

        return addedPathFilter
            ? pathFilter.filter( sourceTargetCompares )
            : QueryBuilders.boolQuery().filter( QueryBuilders.matchAllQuery() ).filter( sourceTargetCompares );
    }

    private QueryBuilder hasPath( final NodePath nodePath, final boolean recursive )
    {
        final String queryPath = nodePath.toString().toLowerCase();

        final BoolQueryBuilder pathQuery = new BoolQueryBuilder().
            should( new TermQueryBuilder( BranchIndexPath.PATH.getPath(), queryPath ) );

        if ( recursive )
        {
            pathQuery.should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(),
                                                        queryPath.endsWith( "/" ) ? queryPath + "*" : queryPath + "/*" ) );
        }

        return pathQuery;
    }

    private BoolQueryBuilder deletedInBranch( final Branch sourceBranch )
    {
        return new BoolQueryBuilder().
            must( isDeleted() ).
            must( new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), sourceBranch.getValue() ) );
    }

    private TermQueryBuilder isDeleted()
    {
        return new TermQueryBuilder( BranchIndexPath.STATE.toString(), NodeState.PENDING_DELETE.value() );
    }

    private TermQueryBuilder isInBranch( final Branch source )
    {
        return createWsConstraint( source );
    }

    private TermQueryBuilder createWsConstraint( final Branch branch )
    {
        return new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), branch.getValue() );
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
            Preconditions.checkNotNull( this.query );
        }

        public DiffQueryFactory build()
        {
            this.validate();
            return new DiffQueryFactory( this );
        }
    }
}
