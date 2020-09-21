package com.enonic.xp.repo.impl.repository;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class RepositoryServiceActivator
{
    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final RepositoryEntryService repositoryEntryService;

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeSearchService nodeSearchService;

    private ServiceRegistration<RepositoryService> service;

    @Activate
    public RepositoryServiceActivator( @Reference final RepositoryEntryService repositoryEntryService,
                                       @Reference final IndexServiceInternal indexServiceInternal,
                                       @Reference final NodeRepositoryService nodeRepositoryService,
                                       @Reference final NodeStorageService nodeStorageService,
                                       @Reference final NodeSearchService nodeSearchService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.nodeStorageService = nodeStorageService;
        this.repositoryEntryService = repositoryEntryService;
        this.nodeRepositoryService = nodeRepositoryService;
        this.nodeSearchService = nodeSearchService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final RepositoryServiceImpl repositoryService =
            new RepositoryServiceImpl( repositoryEntryService, indexServiceInternal, nodeRepositoryService, nodeStorageService,
                                       nodeSearchService );
        repositoryService.initialize();
        service = context.registerService( RepositoryService.class, repositoryService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
