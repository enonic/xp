package com.enonic.wem.api.content.datatype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

/**
 * DataTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public interface DataType
{
    int getKey();

    String getName();

    JavaType.BaseType getJavaType();

    String getIndexableString( Object value );

    void checkValidity( Data data )
        throws InvalidValueException, InvalidValueTypeException;

    void ensureType( Data data )
        throws InconvertibleValueException;

    boolean hasCorrectType( Object value );
}
