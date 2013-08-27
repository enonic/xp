package com.enonic.wem.api.schema.content.form.inputtype;


import com.enonic.wem.api.data.data.Property;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Property property )
        throws InvalidValueException;
}
