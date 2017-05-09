package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.AclFilterBuilderFactory;
import com.enonic.xp.repository.RepositoryId;

public class MultiRepoSearchSourceAdaptor
    extends AbstractSourceAdapter
{

    public static ESSource adapt( final MultiRepoSearchSource source )
    {
        boolean needsBranchFilter = false;

        boolean needsSeparateAclFilter = false;

        final Branches allBranches = source.getAllBranches();

        if ( allBranches.getSize() > 1 )
        {
            needsBranchFilter = true;
        }

        // TODO: Optimize the filters, only apply when needed
        final ESSource.Builder esSourceBuilder = ESSource.create().
            indexNames(
                source.getRepositoryIds().stream().map( AbstractSourceAdapter::createSearchIndexName ).collect( Collectors.toSet() ) ).
            indexTypes( source.getAllBranches().stream().map( AbstractSourceAdapter::createSearchTypeName ).collect( Collectors.toSet() ) ).
            addFilter( createSourceFilters( source ) );

        return esSourceBuilder.build();
    }

    private static Filter createSourceFilters( final MultiRepoSearchSource sources )
    {
        final BooleanFilter.Builder sourceFilters = BooleanFilter.create();

        for ( final SingleRepoSearchSource source : sources )
        {
            sourceFilters.should( createSourceFilter( source ) );
        }

        return sourceFilters.build();
    }

    private static Filter createSourceFilter( final SingleRepoSearchSource source )
    {
        return BooleanFilter.create().
            must( AclFilterBuilderFactory.create( source.getAcl() ) ).
            must( createRepoFilter( source.getRepositoryId() ) ).
            must( createBranchFilter( source.getBranch() ) ).
            build();
    }

    private static Filter createRepoFilter( final RepositoryId repositoryId )
    {
        return ValueFilter.create().
            fieldName( "_index" ).
            addValue( ValueFactory.newString( createSearchIndexName( repositoryId ) ) ).
            build();
    }

    private static Filter createBranchFilter( final Branch branch )
    {
        return ValueFilter.create().
            fieldName( "_type" ).
            addValue( ValueFactory.newString( createSearchTypeName( branch ) ) ).
            build();
    }
}
