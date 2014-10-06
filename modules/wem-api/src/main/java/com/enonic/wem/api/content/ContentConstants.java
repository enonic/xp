package com.enonic.wem.api.content;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public class ContentConstants
{
    public static final Workspace WORKSPACE_STAGE = Workspace.from( "stage" );

    public static final Workspace WORKSPACE_PROD = Workspace.from( "prod" );

    public static final Repository CONTENT_REPO = Repository.create().
        id( RepositoryId.from( "wem-content-repo" ) ).
        build();

    public static final Context CONTEXT_STAGE = ContextBuilder.create().
        object( WORKSPACE_STAGE ).
        object( CONTENT_REPO.getId() ).
        build();

}
