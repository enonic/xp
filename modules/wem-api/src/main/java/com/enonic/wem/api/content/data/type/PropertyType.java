package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

/**
 * PropertyTypes should only be created when:
 * * the type can give something more when indexed
 * * needs validation.
 */
public interface PropertyType
{
    int getKey();

    String getName();

    JavaType.BaseType getJavaType();

    void checkValidity( Property property )
        throws InvalidValueException, InvalidValueTypeException;
}
