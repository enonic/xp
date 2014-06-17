package com.enonic.wem.api.content.page;

import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.rendering.Renderable;

public interface PageComponent
    extends Renderable
{
    ComponentName getName();

    PageComponentType getType();

    ComponentPath getPath();

    void setParent( Region region );
}
