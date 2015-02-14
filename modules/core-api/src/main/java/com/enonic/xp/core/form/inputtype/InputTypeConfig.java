package com.enonic.xp.core.form.inputtype;


import com.enonic.xp.core.data.Property;
import com.enonic.xp.core.form.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Property property )
        throws InvalidValueException;
}
