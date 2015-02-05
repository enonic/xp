package com.enonic.wem.api.security;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public final class SystemConstants
{

    public static final Workspace WORKSPACE_SECURITY = Workspace.create().
        name( "security" ).
        build();

    public static final Repository SYSTEM_REPO = Repository.create().
        id( RepositoryId.from( "system-repo" ) ).
        build();

    public static final Context CONTEXT_SECURITY = ContextBuilder.create().
        workspace( WORKSPACE_SECURITY ).
        repositoryId( SYSTEM_REPO.getId() ).
        build();
}
