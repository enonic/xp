package com.enonic.wem.api.content.type.component.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.component.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Data data )
        throws InvalidValueException;
}
