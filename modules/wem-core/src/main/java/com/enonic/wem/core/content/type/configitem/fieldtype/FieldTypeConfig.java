package com.enonic.wem.core.content.type.configitem.fieldtype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.configitem.InvalidValueException;

/**
 * A FieldTypeConfig can be any kind of class. Currently this interface only works as a marker,
 * since it has no requirements to the implementors.
 */
public interface FieldTypeConfig
{
    void checkValidity( Data data )
        throws InvalidValueException;
}
