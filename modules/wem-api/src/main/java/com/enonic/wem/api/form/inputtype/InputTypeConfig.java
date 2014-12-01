package com.enonic.wem.api.form.inputtype;


import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.form.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Property property )
        throws InvalidValueException;
}
