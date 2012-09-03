package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

/**
 * A Color is a String containing the properties of a colour, separated by semicolon.
 * TODO: or Use java.awt.Color?
 */
public class Color
    extends AbstractDataType
{
    Color( int key )
    {
        super( key, JavaType.STRING, FieldTypes.COLOR );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null;
    }
}
