package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.Collection;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.IndexFilter;
import com.enonic.xp.query.filter.IndicesFilter;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AclFilterBuilderFactory;
import com.enonic.xp.repository.RepositoryId;

class MultiRepoSearchSourceAdaptor
    extends AbstractSourceAdapter
{
    static ESSource adapt( final MultiRepoSearchSource source )
    {
        final ESSource.Builder esSourceBuilder = ESSource.create().
            indexNames( source.getSources().stream().map(
                singleRepoSource -> AbstractSourceAdapter.createSearchIndexName( singleRepoSource.getRepositoryId(),
                                                                                 singleRepoSource.getBranch() ) ).
                collect( Collectors.toSet() ) ).
            addFilter( createSourceFilters( source ) );

        return esSourceBuilder.build();
    }

    private static Filter createSourceFilters( final MultiRepoSearchSource sources )
    {
        final RepoBranchAclMap repoBranchAclMap = RepoBranchAclMap.from( sources );

        final BooleanFilter.Builder sourceFilters = BooleanFilter.create();

        for ( final RepositoryId repoId : repoBranchAclMap )
        {
            sourceFilters.should( createRepoFilter( repoId, repoBranchAclMap.getBranchAclEntries( repoId ) ) );
        }

        return sourceFilters.build();
    }

    private static Filter createRepoFilter( final RepositoryId repoId, final Collection<BranchAclEntry> branchAclEntries )
    {
        if ( branchAclEntries.size() == 1 )
        {
            final BranchAclEntry entry = branchAclEntries.iterator().next();
            return doCreateAclEntryFilter( repoId, entry );
        }

        return createFilterForSeveralBranchesInSameRepo( repoId, branchAclEntries );
    }

    private static Filter createFilterForSeveralBranchesInSameRepo( final RepositoryId repoId,
                                                                    final Collection<BranchAclEntry> branchAclEntries )
    {
        final BooleanFilter.Builder builder = BooleanFilter.create();

        for ( final BranchAclEntry entry : branchAclEntries )
        {
            builder.should( doCreateAclEntryFilter( repoId, entry ) );
        }

        return builder.build();
    }

    private static Filter doCreateAclEntryFilter( final RepositoryId repoId, final BranchAclEntry entry )
    {
        final BooleanFilter.Builder filters = BooleanFilter.create().
            must( createBranchFilter( entry.getBranch() ) );

        final Filter aclFilter = AclFilterBuilderFactory.create( entry.getAcl() );
        if ( aclFilter != null )
        {
            filters.must( aclFilter );
        }

        filters.must( IndexFilter.create().
            value( createSearchIndexName( repoId, entry.getBranch() ) ).
            build() );

        return IndicesFilter.create().
            addIndex( createSearchIndexName( repoId, entry.getBranch() ) ).
            filter( filters.
                build() ).
            build();
    }

    private static Filter createBranchFilter( final Branch branch )
    {
        // USE ID-FILTER TO KEEP CASE
        return IdFilter.create().
            fieldName( "branch" ).
            value( createSearchTypeName( branch ) ).
            build();
    }
}
