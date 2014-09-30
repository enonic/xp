package com.enonic.wem.core;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.workspace.Workspaces;

public class TestContext
{
    public static final Workspace TEST_WORKSPACE = Workspace.from( "test" );

    public static final Repository TEST_REPOSITORY = Repository.create().
        id( RepositoryId.from( "test" ) ).
        workspaces( Workspaces.from( TEST_WORKSPACE ) ).
        build();

    public static final Context TEST_CONTEXT = Context.create().
        workspace( TEST_WORKSPACE ).
        repository( TEST_REPOSITORY ).
        build();


}
