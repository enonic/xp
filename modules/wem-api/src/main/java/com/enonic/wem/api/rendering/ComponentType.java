package com.enonic.wem.api.rendering;


import com.enonic.wem.api.Client;

public interface ComponentType<T extends Component>
{
    public RenderingResult execute( T component, Context context, Client client );
}
