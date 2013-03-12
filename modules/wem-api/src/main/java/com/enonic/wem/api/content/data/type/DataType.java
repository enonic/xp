package com.enonic.wem.api.content.data.type;


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

    void checkValidity( Data data )
        throws InvalidValueException, InvalidValueTypeException;

    boolean hasCorrectType( Object value );
}
