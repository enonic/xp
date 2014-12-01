package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data2.PropertyTree;

public interface DescriptorBasedPageComponent
    extends PageComponent
{
    PropertyTree getConfig();

    DescriptorKey getDescriptor();
}
