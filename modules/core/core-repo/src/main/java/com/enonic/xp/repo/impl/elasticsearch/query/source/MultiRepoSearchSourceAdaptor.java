package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.Collection;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.IndicesFilter;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AclFilterBuilderFactory;
import com.enonic.xp.repository.RepositoryId;

class MultiRepoSearchSourceAdaptor
    extends AbstractSourceAdapter
{
    static ESSource adapt( final MultiRepoSearchSource source )
    {
        final ESSource.Builder esSourceBuilder = ESSource.create().
            indexNames(
                source.getRepositoryIds().stream().map( AbstractSourceAdapter::createSearchIndexName ).collect( Collectors.toSet() ) ).
            indexTypes( source.getAllBranches().stream().map( AbstractSourceAdapter::createSearchTypeName ).collect( Collectors.toSet() ) ).
            addFilter( createSourceFilters( source ) );

        return esSourceBuilder.build();
    }

    private static Filter createSourceFilters( final MultiRepoSearchSource sources )
    {
        final RepoBranchAclMap repoBranchAclMap = RepoBranchAclMap.from( sources );

        final BooleanFilter.Builder sourceFilters = BooleanFilter.create();

        for ( final RepositoryId repoId : repoBranchAclMap )
        {
            sourceFilters.must( createRepoFilter( repoId, repoBranchAclMap.getBranchAclEntries( repoId ) ) );
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
        return IndicesFilter.create().
            addIndex( createSearchIndexName( repoId ) ).
            filter( BooleanFilter.create().
                must( AclFilterBuilderFactory.create( entry.getAcl() ) ).
                must( createBranchFilter( entry.getBranch() ) ).
                build() ).
            build();
    }

    private static Filter createBranchFilter( final Branch branch )
    {
        // USE ID-FILTER TO KEEP CASE
        return IdFilter.create().
            fieldName( "_type" ).
            value( createSearchTypeName( branch ) ).
            build();
    }
}
