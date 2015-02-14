package com.enonic.xp.form.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Property property )
        throws InvalidValueException;
}
