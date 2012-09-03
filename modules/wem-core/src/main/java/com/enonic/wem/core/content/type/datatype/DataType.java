package com.enonic.wem.core.content.type.datatype;


import com.enonic.wem.core.content.type.configitem.InvalidValueException;
import com.enonic.wem.core.content.type.configitem.fieldtype.FieldType;

/**
 * DataTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public interface DataType
{
    int getKey();

    String getName();

    public FieldType getDefaultFieldType();

    JavaType getJavaType();

    String getIndexableString( Object value );

    String convertToString( Object value );

    boolean isConvertibleTo( JavaType date );

    void checkValidity( Object value )
        throws InvalidValueException;

    Object ensureType( Object value )
        throws InconvertibleException;
}
