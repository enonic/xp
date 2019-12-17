package com.enonic.xp.core.impl.app.descriptor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;

@Component
public final class DescriptorServiceImpl
    implements DescriptorService
{
    private final DescriptorLoaderMap map = new DescriptorLoaderMap();

    @Override
    public <T extends Descriptor> T get( final Class<T> type, final DescriptorKey key )
    {
        return getFacet( type ).get( key );
    }

    @Override
    public <T extends Descriptor> Descriptors<T> get( final Class<T> type, final DescriptorKeys keys )
    {
        return getFacet( type ).get( keys );
    }

    @Override
    public <T extends Descriptor> Descriptors<T> get( final Class<T> type, final ApplicationKeys keys )
    {
        return getFacet( type ).get( keys );
    }

    @Override
    public <T extends Descriptor> Descriptors<T> getAll( final Class<T> type )
    {
        return getFacet( type ).getAll();
    }

    @Override
    public <T extends Descriptor> DescriptorKeys find( final Class<T> type, final ApplicationKeys keys )
    {
        return getFacet( type ).find( keys );
    }

    @Override
    public <T extends Descriptor> DescriptorKeys findAll( final Class<T> type )
    {
        return getFacet( type ).findAll();
    }

    private <T extends Descriptor> DescriptorFacet<T> getFacet( final Class<T> type )
    {
        return this.map.facet( type );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addLoader( final DescriptorLoader loader )
    {
        this.map.add( loader );
    }

    public void removeLoader( final DescriptorLoader loader )
    {
        this.map.remove( loader );
    }

    @Reference
    public void setFacetFactory( final DescriptorFacetFactory facetFactory )
    {
        this.map.facetFactory = facetFactory;
    }
}
