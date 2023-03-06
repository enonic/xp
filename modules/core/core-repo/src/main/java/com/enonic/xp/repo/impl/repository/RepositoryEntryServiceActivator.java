package com.enonic.xp.repo.impl.repository;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

@Component(immediate = true)
public class RepositoryEntryServiceActivator
{

    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final NodeSearchService nodeSearchService;

    private final EventPublisher eventPublisher;

    private final BinaryService binaryService;

    private ServiceRegistration<RepositoryEntryService> service;

    @Activate
    public RepositoryEntryServiceActivator( @Reference final IndexServiceInternal indexServiceInternal,
                                            @Reference final NodeStorageService nodeStorageService,
                                            @Reference final NodeSearchService nodeSearchService,
                                            @Reference final EventPublisher eventPublisher, @Reference final BinaryService binaryService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
        this.eventPublisher = eventPublisher;
        this.binaryService = binaryService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final RepositoryEntryServiceImpl repositoryEntryService =
            new RepositoryEntryServiceImpl( indexServiceInternal, nodeStorageService, nodeSearchService, eventPublisher, binaryService );

        service = context.registerService( RepositoryEntryService.class, repositoryEntryService, null );
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
