package com.enonic.xp.core.impl.idprovider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class IdProviderDescriptorServiceImpl
    implements IdProviderDescriptorService
{
    private ResourceService resourceService;

    @Override
    public IdProviderDescriptor getDescriptor( final ApplicationKey key )
    {
        final ResourceProcessor<ApplicationKey, IdProviderDescriptor> processor = newProcessor( key );
        return this.resourceService.processResource( processor );
    }

    private ResourceProcessor<ApplicationKey, IdProviderDescriptor> newProcessor( final ApplicationKey key )
    {
        return new ResourceProcessor.Builder<ApplicationKey, IdProviderDescriptor>().key( key )
            .segment( "authDescriptor" )
            .keyTranslator( IdProviderDescriptor::toResourceKey )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
    }

    private IdProviderDescriptor loadDescriptor( final ApplicationKey key, final Resource resource )
    {
        return YmlIdProviderDescriptorParser.parse( resource.readString(), key ).build();
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
