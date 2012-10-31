package com.enonic.wem.api.content.type.component;


public abstract class Layout
    extends Component
{
    Layout()
    {
    }

    public abstract Component getComponent( final String name );
}
