package com.enonic.wem.repo.internal;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

public class TestContext
{
    public static final Branch TEST_BRANCH = Branch.from( "test" );

    public static final Repository TEST_REPOSITORY = Repository.create().
        id( RepositoryId.from( "test" ) ).
        build();

    public static final Context TEST_CONTEXT = ContextBuilder.create().
        branch( TEST_BRANCH ).
        repositoryId( TEST_REPOSITORY.getId() ).
        build();

}
