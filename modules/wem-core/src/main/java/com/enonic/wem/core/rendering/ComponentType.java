package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.page.Component;

public interface ComponentType<T extends Component>
{
    RenderingResult execute( T component, Context context);
}
