package com.enonic.xp.core.security;

import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.core.context.Context;
import com.enonic.xp.core.context.ContextBuilder;
import com.enonic.xp.core.repository.Repository;
import com.enonic.xp.core.repository.RepositoryId;

public final class SystemConstants
{

    public static final Branch BRANCH_SECURITY = Branch.create().
        name( "master" ).
        build();

    public static final Repository SYSTEM_REPO = Repository.create().
        id( RepositoryId.from( "system-repo" ) ).
        build();

    public static final Context CONTEXT_SECURITY = ContextBuilder.create().
        branch( BRANCH_SECURITY ).
        repositoryId( SYSTEM_REPO.getId() ).
        build();
}
