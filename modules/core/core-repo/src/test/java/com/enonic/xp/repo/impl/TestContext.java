package com.enonic.xp.repo.impl;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class TestContext
{
    public static final BranchId TEST_BRANCH_ID = BranchId.from( "test" );

    public static final Repository TEST_REPOSITORY = Repository.create().
        id( RepositoryId.from( "test" ) ).
        build();

    public static final Context TEST_CONTEXT = ContextBuilder.create().
        branch( TEST_BRANCH_ID ).
        repositoryId( TEST_REPOSITORY.getId() ).
        build();

}
