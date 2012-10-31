package com.enonic.wem.api.content.type.formitem.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Data data )
        throws InvalidValueException;
}
