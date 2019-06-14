package com.enonic.xp.security;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;

public class SecurityConstants
{
    public static final Branch BRANCH_SECURITY = SystemConstants.BRANCH_SYSTEM;

    public static final BranchInfo BRANCH_INFO_SECURITY = SystemConstants.BRANCH_INFO_SYSTEM;

    public static final Repository SECURITY_REPO = SystemConstants.SYSTEM_REPO;

    public static final Context CONTEXT_SECURITY = ContextBuilder.create().
        branch( BRANCH_SECURITY ).
        repositoryId( SECURITY_REPO.getId() ).
        build();

}
