package com.enonic.xp.repo.impl.elasticsearch.query.source;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;

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

public class MultiRepoSearchSourceAdaptorTest
{
    @Test
    public void single_repo()
        throws Exception
    {
        final ESSource source = MultiRepoSearchSourceAdaptor.adapt( MultiRepoSearchSource.create().
            add( SingleRepoSearchSource.create().
                repositoryId( RepositoryId.from( "repo1" ) ).
                branch( Branch.from( "branch1" ) ).
                acl( PrincipalKeys.from( PrincipalKey.ofAnonymous() ) ).
                build() ).
            build() );

        assertEquals( "search-repo1-branch1", source.getIndexNames().iterator().next() );
    }


    @Test
    public void su_user_yields_no_acl_filter()
        throws Exception
    {
        final ESSource source = MultiRepoSearchSourceAdaptor.adapt( MultiRepoSearchSource.create().
            add( SingleRepoSearchSource.create().
                repositoryId( RepositoryId.from( "repo1" ) ).
                branch( Branch.from( "branch1" ) ).
                acl( PrincipalKeys.from( RoleKeys.ADMIN ) ).
                build() ).
            build() );

        assertEquals( 1, source.getFilters().getSize() );
        assertEquals( "search-repo1-branch1", source.getIndexNames().iterator().next() );
    }

    @Test
    public void multiple_repos()
        throws Exception
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

        assertTrue( source.getIndexNames().containsAll( Arrays.asList( "search-repo1-branch1", "search-repo2-branch2" ) ) );

        final Filters filters = source.getFilters();

        assertEquals( 1, filters.getSize() );
        final Filter allFilters = filters.get( 0 );
        assert ( allFilters instanceof BooleanFilter );
        final ImmutableSet<Filter> shouldFilters = ( (BooleanFilter) allFilters ).getShould();
        assertEquals( 2, shouldFilters.size() );
    }
}
