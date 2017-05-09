package com.enonic.xp.core.impl.app.descriptor;

import com.enonic.xp.descriptor.Descriptor;
import com.enonic.xp.descriptor.DescriptorLoader;

public interface DescriptorFacetFactory
{
    <T extends Descriptor> DescriptorFacet<T> create( DescriptorLoader<T> loader );
}
