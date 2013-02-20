package com.enonic.wem.api.content.schema.type.form.inputtype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.schema.type.form.InvalidValueException;

public interface InputTypeConfig
{
    void checkValidity( Data data )
        throws InvalidValueException;
}
