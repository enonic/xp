package com.enonic.wem.api.content.type.formitem;


public abstract class Layout
    extends Component
{
    Layout()
    {
    }

    public abstract Component getComponent( final String name );
}
