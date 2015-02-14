package com.enonic.xp.admin.impl.rest.resource.repo;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.core.branch.Branches;
import com.enonic.xp.core.repository.RepositoryId;

class ReindexRequestJson
{
    private final RepositoryId repositoryId;

    private final boolean initialize;

    private final Branches branches;

    @JsonCreator
    public ReindexRequestJson( @JsonProperty("repository") final String repositoryId, //
                               @JsonProperty("initialize") final boolean initialize,  //
                               @JsonProperty("branches") final String branchesString )
    {
        this.repositoryId = RepositoryId.from( repositoryId );
        this.initialize = initialize;

        final Set<Branch> branchesSet = Sets.newHashSet();

        final String[] branches = branchesString.split( "," );

        for ( final String branch : branches )
        {
            branchesSet.add( Branch.from( branch ) );
        }

        this.branches = Branches.from( branchesSet );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public boolean isInitialize()
    {
        return initialize;
    }

    public Branches getBranches()
    {
        return branches;
    }
}
