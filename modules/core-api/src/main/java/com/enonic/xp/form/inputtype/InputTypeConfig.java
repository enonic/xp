package com.enonic.xp.form.inputtype;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

@Beta
public interface InputTypeConfig
{
    void checkValidity( Property property )
        throws InvalidValueException;
}
