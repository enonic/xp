package com.enonic.xp.repo.impl.elasticsearch.query;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.HasChildFilterBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.repo.impl.StorageType;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class DiffQueryFactory
{
    private final BranchId source;

    private final BranchId target;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private final StorageType childStorageType;

    private DiffQueryFactory( Builder builder )
    {
        source = builder.query.getSource();
        target = builder.query.getTarget();
        nodePath = builder.query.getNodePath();
        childStorageType = builder.childStorageType;
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
        final BoolFilterBuilder inSourceOnly = onlyInQuery( this.source, this.target );

        final BoolFilterBuilder inTargetOnly = onlyInQuery( this.target, this.source );

        final BoolFilterBuilder deletedInSourceOnly = deletedOnlyQuery( this.source, this.target );

        final BoolFilterBuilder deletedInTargetOnly = deletedOnlyQuery( this.target, this.source );

        final BoolFilterBuilder sourceTargetCompares =
            joinOnlyInQueries( inSourceOnly, inTargetOnly, deletedInSourceOnly, deletedInTargetOnly );

        return wrapInPathQueryIfNecessary( sourceTargetCompares );
    }

    private BoolFilterBuilder deletedOnlyQuery( final BranchId source, final BranchId target )
    {
        return new BoolFilterBuilder().
            must( deletedInBranch( source ) ).
            mustNot( deletedInBranch( target ) );
    }

    private BoolFilterBuilder onlyInQuery( final BranchId source, final BranchId target )
    {
        return new BoolFilterBuilder().
            must( isInBranch( source ) ).
            mustNot( isInBranch( target ) );
    }

    private FilteredQueryBuilder wrapInPathQueryIfNecessary( final BoolFilterBuilder sourceTargetCompares )
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
            ? new FilteredQueryBuilder( pathFilter, sourceTargetCompares )
            : new FilteredQueryBuilder( QueryBuilders.matchAllQuery(), sourceTargetCompares );
    }

    private BoolFilterBuilder joinOnlyInQueries( final BoolFilterBuilder inSourceOnly, final BoolFilterBuilder inTargetOnly,
                                                 final BoolFilterBuilder deletedInSourceOnly, final BoolFilterBuilder deletedInTargetOnly )
    {
        return new BoolFilterBuilder().
            should( inSourceOnly ).
            should( inTargetOnly ).
            should( deletedInSourceOnly ).
            should( deletedInTargetOnly );
    }

    private QueryBuilder hasPath( final NodePath nodePath, final boolean recursive )
    {
        final String queryPath = nodePath.toString();

        final BoolQueryBuilder pathQuery = new BoolQueryBuilder().
            should( new TermQueryBuilder( BranchIndexPath.PATH.getPath(), queryPath ) );

        if ( recursive )
        {
            pathQuery.should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(),
                                                        queryPath.endsWith( "/" ) ? queryPath + "*" : queryPath + "/*" ) );
        }

        return new HasChildQueryBuilder( childStorageType.getName(), pathQuery );
    }

    private HasChildFilterBuilder deletedInBranch( final BranchId sourceBranchId )
    {
        return new HasChildFilterBuilder( childStorageType.getName(), new BoolFilterBuilder().
            must( isDeleted() ).
            must( new TermFilterBuilder( BranchIndexPath.BRANCH_NAME.toString(), sourceBranchId.getValue() ) ) );
    }

    private TermFilterBuilder isDeleted()
    {
        return new TermFilterBuilder( BranchIndexPath.STATE.toString(), NodeState.PENDING_DELETE.value() );
    }

    private HasChildFilterBuilder isInBranch( final BranchId source )
    {
        return new HasChildFilterBuilder( childStorageType.getName(), createWsConstraint( source ) );
    }

    private TermFilterBuilder createWsConstraint( final BranchId branchId )
    {
        return new TermFilterBuilder( BranchIndexPath.BRANCH_NAME.toString(), branchId );
    }

    public static final class Builder
    {
        private NodeVersionDiffQuery query;

        private StorageType childStorageType;

        private Builder()
        {
        }

        public Builder query( NodeVersionDiffQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder childStorageType( final StorageType childStorageType )
        {
            this.childStorageType = childStorageType;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.query );
            Preconditions.checkNotNull( this.childStorageType );
        }

        public DiffQueryFactory build()
        {
            this.validate();
            return new DiffQueryFactory( this );
        }
    }
}
