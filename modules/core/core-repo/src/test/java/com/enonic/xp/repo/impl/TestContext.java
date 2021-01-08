package com.enonic.xp.repo.impl;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public class TestContext
{
    public static final Branch TEST_BRANCH = Branch.from( "test" );

    public static final RepositoryId TEST_REPOSITORY_ID = RepositoryId.from( "test" );
}
