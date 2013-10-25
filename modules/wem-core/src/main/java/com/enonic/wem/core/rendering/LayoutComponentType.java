package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.page.Layout;
import com.enonic.wem.core.rendering.ComponentType;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderingResult;

public class LayoutComponentType
    implements ComponentType<Layout>
{
    @Override
    public RenderingResult execute( final Layout layout, final Context context )
    {
        return null;
    }
}
