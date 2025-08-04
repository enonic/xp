package com.enonic.xp.core.impl.app.descriptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
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
import com.enonic.xp.descriptor.DescriptorKey;

@Component
public final class DescriptorServiceImpl
    implements DescriptorService
{
    private final Map<Class<?>, DescriptorLoader<?>> map = new ConcurrentHashMap<>();

    private final DescriptorFacetFactory facetFactory;

    @Activate
    public DescriptorServiceImpl( @Reference final DescriptorFacetFactory facetFactory )
    {
        this.facetFactory = facetFactory;
    }

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
        final DescriptorLoader<T> loader = (DescriptorLoader<T>) this.map.get( type );
        if ( loader == null )
        {
            return new NopDescriptorFacet<>();
        }

        return this.facetFactory.create( loader );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addLoader( final DescriptorLoader loader )
    {
        this.map.put( loader.getType(), loader );
    }

    public void removeLoader( final DescriptorLoader loader )
    {
        this.map.remove( loader.getType() );
    }
}
