package com.enonic.wem.api.rendering;


public interface ComponentType<T extends Component>
{
    RenderingResult execute( T component, Context context);
}
