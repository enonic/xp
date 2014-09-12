package com.enonic.wem.core;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.Workspaces;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;

public class TestContext
{
    public static Workspace TEST_WORKSPACE = Workspace.from( "test" );

    public static Repository TEST_REPOSITORY = Repository.create().
        id( RepositoryId.from( "test" ) ).
        workspaces( Workspaces.from( TEST_WORKSPACE ) ).
        build();

    public static Context TEST_CONTEXT = Context.create().
        workspace( TEST_WORKSPACE ).
        repository( TEST_REPOSITORY ).
        build();


}
