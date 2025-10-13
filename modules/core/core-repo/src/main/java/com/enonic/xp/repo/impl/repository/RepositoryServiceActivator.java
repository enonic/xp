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

    private ServiceRegistration<?> service;

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
        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( repositoryService ).
            setNodeStorageService( nodeStorageService ).
            build().
            initialize();

        new Xp8IndexMigrator( repositoryService, indexServiceInternal).migrate();

        service = context.registerService( new String[]{RepositoryService.class.getName(), InternalRepositoryService.class.getName()},
                                           repositoryService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
