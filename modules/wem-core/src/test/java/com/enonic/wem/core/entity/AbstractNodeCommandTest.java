package com.enonic.wem.core.entity;

import org.mockito.Mockito;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.Workspaces;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.version.VersionService;
import com.enonic.wem.core.workspace.WorkspaceService;

public abstract class AbstractNodeCommandTest
{
    protected final NodeDao nodeDao = Mockito.mock( NodeDao.class );

    protected final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    protected final VersionService versionService = Mockito.mock( VersionService.class );

    protected final IndexService indexService = Mockito.mock( IndexService.class );

    public final Workspace testWorkspace = Workspace.from( "test" );

    protected final Context testContext = Context.create().
        workspace( testWorkspace ).
        repository( Repository.create().
            id( RepositoryId.from( "testing" ) ).
            workspaces( Workspaces.from( testWorkspace ) ).
            build() ).
        build();
}
