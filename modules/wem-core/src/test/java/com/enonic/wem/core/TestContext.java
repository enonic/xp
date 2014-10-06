package com.enonic.wem.core;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;

public class TestContext
{
    public static final Workspace TEST_WORKSPACE = Workspace.from( "test" );

    public static final Repository TEST_REPOSITORY = Repository.create().
        id( RepositoryId.from( "test" ) ).
        build();

    public static final Context TEST_CONTEXT = Context.create().
        workspace( TEST_WORKSPACE ).
        repositoryId( TEST_REPOSITORY.getId() ).
        build();


}
