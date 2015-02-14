package com.enonic.xp.security;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;

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
