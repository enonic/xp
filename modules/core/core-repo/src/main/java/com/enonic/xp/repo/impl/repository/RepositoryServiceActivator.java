package com.enonic.xp.repo.impl.repository;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.internal.InternalRepositoryService;

@Component(immediate = true)
public class RepositoryServiceActivator
{
    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final RepositoryEntryService repositoryEntryService;

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeSearchService nodeSearchService;

    private final BranchService branchService;

    private final RepositoryAuditLogSupport repositoryAuditLogSupport;

    private ServiceRegistration<?> service;

    @Activate
    public RepositoryServiceActivator( @Reference final RepositoryEntryService repositoryEntryService,
                                       @Reference final IndexServiceInternal indexServiceInternal,
                                       @Reference final NodeRepositoryService nodeRepositoryService,
                                       @Reference final NodeStorageService nodeStorageService,
                                       @Reference final NodeSearchService nodeSearchService,
                                       @Reference final BranchService branchService,
                                       @Reference final RepositoryAuditLogSupport repositoryAuditLogSupport )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.nodeStorageService = nodeStorageService;
        this.repositoryEntryService = repositoryEntryService;
        this.nodeRepositoryService = nodeRepositoryService;
        this.nodeSearchService = nodeSearchService;
        this.branchService = branchService;
        this.repositoryAuditLogSupport = repositoryAuditLogSupport;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, nodeRepositoryService, nodeStorageService, nodeSearchService,
                                       branchService, repositoryAuditLogSupport );
        SystemRepoInitializer.create()
            .setIndexServiceInternal( indexServiceInternal )
            .setNodeStorageService( nodeStorageService )
            .setRepositoryEntryService( repositoryEntryService )
            .setNodeRepositoryService( nodeRepositoryService )
            .build()
            .initialize();
        service = context.registerService( new String[]{RepositoryService.class.getName(), InternalRepositoryService.class.getName()},
                                           repositoryService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
