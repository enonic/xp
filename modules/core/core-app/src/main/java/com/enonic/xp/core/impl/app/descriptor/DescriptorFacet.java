package com.enonic.xp.core.impl.app.descriptor;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.Descriptors;

public interface DescriptorFacet<T extends Descriptor>
{
    @Nullable T get( DescriptorKey key );

    Descriptors<T> get( DescriptorKeys keys );

    Descriptors<T> get( ApplicationKeys keys );

    Descriptors<T> getAll();

    DescriptorKeys find( ApplicationKeys keys );

    DescriptorKeys findAll();
}
