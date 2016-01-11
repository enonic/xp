package com.enonic.xp.core.impl.app;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.security.SystemConstants;

class ApplicationConstants
{
    private static final Branch BRANCH_APPLICATIONS = SystemConstants.BRANCH_SYSTEM;

    private static final Repository APPLICATIONS_REPO = SystemConstants.SYSTEM_REPO;

    static final Context CONTEXT_APPLICATIONS = ContextBuilder.create().
        branch( BRANCH_APPLICATIONS ).
        repositoryId( APPLICATIONS_REPO.getId() ).
        build();
}
