package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.PropertyTree;

public interface DescriptorBasedPageComponent
    extends PageComponent
{
    PropertyTree getConfig();

    DescriptorKey getDescriptor();
}
