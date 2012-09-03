package com.enonic.wem.core.content.datatype;


import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class Computed
    extends AbstractDataType
{
    Computed( int key )
    {
        super( key, JavaType.STRING, FieldTypes.VIRTUAL );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null; // TODO
    }
}
