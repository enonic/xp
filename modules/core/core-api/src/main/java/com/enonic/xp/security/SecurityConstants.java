package com.enonic.xp.security;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;

public class SecurityConstants
{
    public static final BranchId BRANCH_ID_SECURITY = SystemConstants.BRANCH_ID_SYSTEM;

    public static final Repository SECURITY_REPO = SystemConstants.SYSTEM_REPO;

    public static final Context CONTEXT_SECURITY = ContextBuilder.create().
        branch( BRANCH_ID_SECURITY ).
        repositoryId( SECURITY_REPO.getId() ).
        build();

}
