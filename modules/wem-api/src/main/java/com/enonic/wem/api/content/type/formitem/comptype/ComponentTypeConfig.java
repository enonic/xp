package com.enonic.wem.api.content.type.formitem.comptype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;

/**
 * A ComponentTypeConfig can be any kind of class. Currently this interface only works as a marker,
 * since it has no requirements to the implementors.
 */
public interface ComponentTypeConfig
{
    void checkValidity( Data data )
        throws InvalidValueException;
}
