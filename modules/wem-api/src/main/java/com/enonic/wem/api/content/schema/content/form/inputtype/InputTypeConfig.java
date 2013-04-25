package com.enonic.wem.api.content.schema.content.form.inputtype;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Property property )
        throws InvalidValueException;
}
