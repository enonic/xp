package com.enonic.xp.repo.impl.vacuum.versiontable;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.RepositoryService;

public class VersionTableVacuumTaskTest
{
    private NodeService nodeService;

    private RepositoryService repositoryService;

    private VersionService versionService;

    public void setUp()
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );
        this.versionService = Mockito.mock( VersionService.class );
    }

    @Test
    public void test()
    {
        final VersionTableVacuumTask task = new VersionTableVacuumTask();
        task.setNodeService( nodeService );
        task.setRepositoryService( repositoryService );
        task.setVersionService( versionService );
    }
}
