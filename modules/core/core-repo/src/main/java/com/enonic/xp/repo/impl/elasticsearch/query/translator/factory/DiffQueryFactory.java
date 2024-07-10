package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.HasChildQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.StorageType;
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
        final BoolQueryBuilder inSourceOnly = onlyInQuery( this.source, this.target );

        final BoolQueryBuilder inTargetOnly = onlyInQuery( this.target, this.source );

        final BoolQueryBuilder sourceTargetCompares = joinOnlyInQueries( inSourceOnly, inTargetOnly );

        return wrapInPathQueryIfNecessary( sourceTargetCompares );
    }

    private BoolQueryBuilder onlyInQuery( final Branch source, final Branch target )
    {
        return new BoolQueryBuilder().must( isInBranch( source ) ).mustNot( isInBranch( target ) );
    }

    private QueryBuilder wrapInPathQueryIfNecessary( final BoolQueryBuilder sourceTargetCompares )
    {

        final BoolQueryBuilder result = QueryBuilders.boolQuery().filter( sourceTargetCompares );

        if ( this.nodePath != null && !this.nodePath.isRoot() )
        {
            result.filter( hasPaths( ExcludeEntries.create().add( new ExcludeEntry( this.nodePath, true ) ).build() ) );
        }

        if ( !this.excludes.isEmpty() )
        {
            result.mustNot( hasPaths( excludes ) );
        }

        return result;
    }

    private BoolQueryBuilder joinOnlyInQueries( final BoolQueryBuilder inSourceOnly, final BoolQueryBuilder inTargetOnly )
    {
        return new BoolQueryBuilder().should( inSourceOnly ).should( inTargetOnly );
    }

    private QueryBuilder hasPaths( final ExcludeEntries excludeEntries )
    {
        final BoolQueryBuilder pathQuery = new BoolQueryBuilder().should( new TermsQueryBuilder( BranchIndexPath.PATH.getPath(),
                                                                                                 excludeEntries.getSet()
                                                                                                     .stream()
                                                                                                     .map(
                                                                                                         excludeEntry -> excludeEntry.nodePath()
                                                                                                             .toString()
                                                                                                             .toLowerCase() )
                                                                                                     .collect( Collectors.toList() ) ) );

        final Set<NodePath> recursivePaths =
            excludeEntries.getSet().stream().filter( ExcludeEntry::recursive ).map( ExcludeEntry::nodePath ).collect( Collectors.toSet() );

        if ( !recursivePaths.isEmpty() )
        {
            final String queryPath = nodePath.toString().toLowerCase();

            recursivePaths.forEach( nodePath -> {
                pathQuery.should( new WildcardQueryBuilder( BranchIndexPath.PATH.getPath(),
                                                            queryPath.endsWith( "/" ) ? queryPath + "*" : queryPath + "/*" ) );
            } );
        }

        return new HasChildQueryBuilder( childStorageType.getName(), pathQuery );
    }

    private HasChildQueryBuilder isInBranch( final Branch source )
    {
        return new HasChildQueryBuilder( childStorageType.getName(), createWsConstraint( source ) );
    }

    private TermQueryBuilder createWsConstraint( final Branch branch )
    {
        return new TermQueryBuilder( BranchIndexPath.BRANCH_NAME.toString(), branch );
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
