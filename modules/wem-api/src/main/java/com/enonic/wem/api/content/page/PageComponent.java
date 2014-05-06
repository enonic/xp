package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.rendering.Renderable;

public interface PageComponent
    extends Renderable
{
    ComponentName getName();

    PageComponentType getType();

    RootDataSet getConfig();

    DescriptorKey getDescriptor();

    ComponentPath getPath();

    void setPath( ComponentPath componentPath );
}
