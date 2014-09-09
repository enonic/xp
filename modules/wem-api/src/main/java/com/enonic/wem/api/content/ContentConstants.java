package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.Workspaces;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;

public class ContentConstants
{
    public static final Workspace WORKSPACE_STAGE = Workspace.from( "stage" );

    public static final Workspace WORKSPACE_PROD = Workspace.from( "prod" );

    public static final Repository CONTENT_REPO = Repository.create().
        id( RepositoryId.from( "wem-content-repo" ) ).
        workspaces( Workspaces.from( WORKSPACE_STAGE, WORKSPACE_PROD ) ).
        build();

    public static final Context CONTEXT_STAGE = Context.create().
        workspace( WORKSPACE_STAGE ).
        repository( CONTENT_REPO ).
        build();

}
