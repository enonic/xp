package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

public class MultiRepoNodeHandleContext
{
    private final List<SearchTarget> searchTargets = new ArrayList<>();

    public MultiRepoNodeHandleContext()
    {
    }

    @SuppressWarnings("unused")
    public void addSource( final String repoId, final String branchId, final String[] principalKeys )
    {
        final SearchTarget.Builder builder = SearchTarget.create().
            repositoryId( RepositoryId.from( repoId ) ).
            branch( Branch.from( branchId ) );

        if ( principalKeys != null )
        {
            builder.principalKeys( PrincipalKeys.from( principalKeys ) );
        }

        this.searchTargets.add( builder.build() );
    }

    List<SearchTarget> getSearchTargets()
    {
        return this.searchTargets;
    }
}
