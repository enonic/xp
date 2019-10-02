package com.enonic.xp.repo.impl.vacuum.versiontable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.vacuum.VacuumTask;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.vacuum.VacuumTaskResult;

@Component(immediate = true)
public class VersionTableVacuumTask
    implements VacuumTask
{
    private static final int ORDER = 100;

    private static final String NAME = "VersionTableVacuumTask";

    private NodeService nodeService;

    private RepositoryService repositoryService;

    private VersionService versionService;

    @Override
    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        return VersionTableVacuumCommand.create().
            repositoryService( repositoryService ).
            nodeService( nodeService ).
            versionService( versionService ).
            params( params ).
            build().
            execute().
            taskName( NAME ).
            build();
    }

    @Override
    public int order()
    {
        return ORDER;
    }

    @Override
    public String name()
    {
        return NAME;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }
}
