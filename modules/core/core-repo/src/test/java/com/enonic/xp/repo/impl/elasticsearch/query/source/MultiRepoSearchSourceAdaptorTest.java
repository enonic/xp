package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiRepoSearchSourceAdaptorTest
{
    @Test
    void single_repo()
    {
        final ESSource source = MultiRepoSearchSourceAdaptor.adapt( MultiRepoSearchSource.create().
            add( SingleRepoSearchSource.create().
                repositoryId( RepositoryId.from( "repo1" ) ).
                branch( Branch.from( "branch1" ) ).
                acl( PrincipalKeys.from( PrincipalKey.ofAnonymous() ) ).
                build() ).
            build() );

        assertEquals( "search-repo1", source.getIndexNames().iterator().next() );
        assertEquals( "branch1", source.getIndexTypes().iterator().next() );
    }


    @Test
    void su_user_yields_no_acl_filter()
    {
        final ESSource source = MultiRepoSearchSourceAdaptor.adapt( MultiRepoSearchSource.create().
            add( SingleRepoSearchSource.create().
                repositoryId( RepositoryId.from( "repo1" ) ).
                branch( Branch.from( "branch1" ) ).
                acl( PrincipalKeys.from( RoleKeys.ADMIN ) ).
                build() ).
            build() );

        assertEquals( 1, source.getFilters().getSize() );
        assertEquals( "search-repo1", source.getIndexNames().iterator().next() );
        assertEquals( "branch1", source.getIndexTypes().iterator().next() );
    }

    @Test
    void multiple_repos()
    {
        final ESSource source = MultiRepoSearchSourceAdaptor.adapt( MultiRepoSearchSource.create().
            add( SingleRepoSearchSource.create().
                repositoryId( RepositoryId.from( "repo1" ) ).
                branch( Branch.from( "branch1" ) ).
                acl( PrincipalKeys.from( PrincipalKey.ofAnonymous() ) ).
                build() ).
            add( SingleRepoSearchSource.create().
                repositoryId( RepositoryId.from( "repo2" ) ).
                branch( Branch.from( "branch2" ) ).
                acl( PrincipalKeys.from( PrincipalKey.ofAnonymous() ) ).
                build() ).
            build() );

        assertTrue( source.getIndexNames().containsAll( Arrays.asList( "search-repo1", "search-repo2" ) ) );
        assertTrue( source.getIndexTypes().containsAll( Arrays.asList( "branch1", "branch2" ) ) );

        final Filters filters = source.getFilters();

        assertEquals( 1, filters.getSize() );
        final Filter allFilters = filters.get( 0 );
        assert ( allFilters instanceof BooleanFilter );
        final List<Filter> shouldFilters = ( (BooleanFilter) allFilters ).getMust();
        assertEquals( 2, shouldFilters.size() );
    }
}
