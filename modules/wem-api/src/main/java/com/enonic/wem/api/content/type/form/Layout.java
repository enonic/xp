package com.enonic.wem.api.content.type.form;


public abstract class Layout
    extends Component
{
    Layout()
    {
    }

    public abstract Component getComponent( final String name );
}
