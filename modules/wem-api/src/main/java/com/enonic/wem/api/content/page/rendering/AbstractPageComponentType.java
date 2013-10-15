package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.rendering.ComponentType;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

public abstract class AbstractPageComponentType<T extends PageComponent>
    implements ComponentType<T>
{
    @Override
    public abstract RenderingResult execute( final T pageComponent, final Context context, final Client client );
}
