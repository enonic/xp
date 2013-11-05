package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.page.Component;

public interface ComponentExecutor<T extends Component>
{
    RenderingResult execute( T component, Context context );
}
