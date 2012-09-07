package com.enonic.wem.core.content.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.InvalidValueException;

/**
 * DataTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public interface DataType
{
    int getKey();

    String getName();

    JavaType getJavaType();

    String getIndexableString( Object value );

    String convertToString( Object value );

    boolean isConvertibleTo( JavaType date );

    void checkValidity( Object value )
        throws InvalidValueException;

    Object ensureType( Object value )
        throws InconvertibleException;

    void ensureType( Data data )
        throws InconvertibleException;
}
