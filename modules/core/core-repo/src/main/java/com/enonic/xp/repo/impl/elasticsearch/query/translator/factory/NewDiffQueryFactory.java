package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.join.query.HasChildQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.StorageType;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.ExcludeEntries;
import com.enonic.xp.repo.impl.version.search.ExcludeEntry;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class NewDiffQueryFactory
{
    private final Branch source;

    private final Branch target;

    private final NodePath nodePath;

    private final ExcludeEntries excludes;

    private final StorageType childStorageType;

    private NewDiffQueryFactory( Builder builder )
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
        final BoolQueryBuilder inSourceOnly = onlyInQuery( this.source, this.target );

        final BoolQueryBuilder inTargetOnly = onlyInQuery( this.target, this.source );

        final BoolQueryBuilder sourceTargetCompares =
            joinOnlyInQueries( inSourceOnly, inTargetOnly/*, deletedInSourceOnly, deletedInTargetOnly*/ );

        return wrapInPathQueryIfNecessary( sourceTargetCompares );
    }

    private BoolQueryBuilder onlyInQuery( final Branch source, final Branch target )
    {
        return new BoolQueryBuilder().
            must( isInBranch( source ) ).
            mustNot( isInBranch( target ) );
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

    private BoolQueryBuilder joinOnlyInQueries( final BoolQueryBuilder inSourceOnly, final BoolQueryBuilder inTargetOnly/*,
                                                final NestedQueryBuilder deletedInSourceOnly, final NestedQueryBuilder deletedInTargetOnly */ )
    {
        return new BoolQueryBuilder().
            should( inSourceOnly ).
            should( inTargetOnly );
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

        return new HasChildQueryBuilder( childStorageType.getName(), pathQuery, ScoreMode.None );
    }

    private TermQueryBuilder isInBranch( final Branch source )
    {
        return new TermQueryBuilder( VersionIndexPath.BRANCHES.toString(), source );
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

        public NewDiffQueryFactory build()
        {
            this.validate();
            return new NewDiffQueryFactory( this );
        }
    }
}
