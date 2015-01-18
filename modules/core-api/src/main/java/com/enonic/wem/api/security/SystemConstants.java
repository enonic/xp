package com.enonic.wem.api.security;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public final class SystemConstants
{
    public static final UserStore SYSTEM_USERSTORE = UserStore.newUserStore().key( UserStoreKey.system() ).displayName( "System" ).build();

    public static final Workspace WORKSPACE_USER_STORES = Workspace.create().
        name( "userstores" ).
        build();

    public static final Repository SYSTEM_REPO = Repository.create().
        id( RepositoryId.from( "wem-system-repo" ) ).
        build();

    public static final Context CONTEXT_USER_STORES = ContextBuilder.create().
        workspace( WORKSPACE_USER_STORES ).
        repositoryId( SYSTEM_REPO.getId() ).
        build();
}
