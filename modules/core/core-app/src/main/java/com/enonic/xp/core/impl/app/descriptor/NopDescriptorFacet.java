package com.enonic.xp.core.impl.app.descriptor;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;

final class NopDescriptorFacet<T extends Descriptor>
    implements DescriptorFacet<T>
{
    @Override
    public T get( final DescriptorKey key )
    {
        return null;
    }

    @Override
    public Descriptors<T> get( final DescriptorKeys keys )
    {
        return Descriptors.empty();
    }

    @Override
    public Descriptors<T> getAll()
    {
        return Descriptors.empty();
    }

    @Override
    public DescriptorKeys findAll()
    {
        return DescriptorKeys.empty();
    }

    @Override
    public Descriptors<T> get( final ApplicationKeys keys )
    {
        return Descriptors.empty();
    }

    @Override
    public DescriptorKeys find( final ApplicationKeys keys )
    {
        return DescriptorKeys.empty();
    }
}
