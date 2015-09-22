package com.enonic.wem.repo.internal.elasticsearch.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.storage.StorageType;
import com.enonic.wem.repo.internal.storage.branch.BranchIndexPath;
import com.enonic.wem.repo.internal.version.NodeVersionDiffQuery;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;

public class DiffQueryFactory
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private final StorageType childStorageType;

    private DiffQueryFactory( Builder builder )
    {
        source = builder.query.getSource();
        target = builder.query.getTarget();
        nodePath = builder.query.getNodePath();
        childStorageType = builder.childStorageType;
    }

    public QueryBuilder execute()
    {
        return createDiffQuery();
    }

    private BoolQueryBuilder createDiffQuery()
    {
        final BoolQueryBuilder inSourceOnly = onlyInQuery( this.source, this.target );

        final BoolQueryBuilder inTargetOnly = onlyInQuery( this.target, this.source );

        final BoolQueryBuilder deletedInSourceOnly = deletedOnlyQuery( this.source, this.target );

        final BoolQueryBuilder deletedInTargetOnly = deletedOnlyQuery( this.target, this.source );

        final BoolQueryBuilder sourceTargetCompares =
            joinOnlyInQueries( inSourceOnly, inTargetOnly, deletedInSourceOnly, deletedInTargetOnly );

        return wrapInPathQueryIfNecessary( sourceTargetCompares );
    }

    private BoolQueryBuilder deletedOnlyQuery( final Branch source, final Branch target )
    {
        return new BoolQueryBuilder().
            must( deletedInBranch( source ) ).
            mustNot( deletedInBranch( target ) );
    }

    private BoolQueryBuilder onlyInQuery( final Branch source, final Branch target )
    {
        return new BoolQueryBuilder().
            must( isInBranch( source ) ).
            mustNot( isInBranch( target ) );
    }

    private BoolQueryBuilder wrapInPathQueryIfNecessary( final BoolQueryBuilder sourceTargetCompares )
    {
        if ( this.nodePath != null && !this.nodePath.isRoot() )
        {
            return new BoolQueryBuilder().
                must( hasPath() ).
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

    private HasChildQueryBuilder hasPath()
    {
        final String queryPath = this.nodePath.toString();

        final BoolQueryBuilder pathQuery = new BoolQueryBuilder().
            should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(),
                                              queryPath.endsWith( "/" ) ? queryPath + "*" : queryPath + "/*" ) ).
            should( new TermQueryBuilder( BranchIndexPath.PATH.getPath(), queryPath ) );

        return new HasChildQueryBuilder( childStorageType.getName(), pathQuery );
    }

    private HasChildQueryBuilder deletedInBranch( final Branch sourceBranch )
    {
        return new HasChildQueryBuilder( childStorageType.getName(), new BoolQueryBuilder().
            must( isDeleted() ).
            must( new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), sourceBranch.getName() ) ) );
    }

    private TermQueryBuilder isDeleted()
    {
        return new TermQueryBuilder( BranchIndexPath.STATE.toString(), NodeState.PENDING_DELETE.value() );
    }

    private HasChildQueryBuilder isInBranch( final Branch source )
    {
        return new HasChildQueryBuilder( childStorageType.getName(), createWsConstraint( source ) );
    }

    private TermQueryBuilder createWsConstraint( final Branch branch )
    {
        return new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), branch );
    }

    public static Builder create()
    {
        return new Builder();
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
