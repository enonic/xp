package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.data.Data;

public class Computed
    extends AbstractDataType
    implements DataType
{
    public Computed()
    {
        super( null );
    }

    public boolean validData( final Data data )
    {
        return true;
    }
}
