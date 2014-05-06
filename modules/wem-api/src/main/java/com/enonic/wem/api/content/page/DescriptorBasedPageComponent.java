package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;

public interface DescriptorBasedPageComponent
    extends PageComponent
{
    RootDataSet getConfig();

    DescriptorKey getDescriptor();
}
