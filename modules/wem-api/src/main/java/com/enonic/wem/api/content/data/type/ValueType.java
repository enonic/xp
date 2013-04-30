package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

/**
 * ValueTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public interface ValueType
{
    int getKey();

    String getName();

    JavaType.BaseType getJavaType();

    void checkValidity( Property property )
        throws InvalidValueException, InvalidValueTypeException;

    void checkValidity( Value value )
        throws InvalidValueException, InvalidValueTypeException;
}
