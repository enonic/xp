package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public interface InputTypeConfigSerializer<T extends InputTypeConfig>
{
    T parseConfig( ApplicationKey currentApp, Element elem );

    ObjectNode serializeConfig( T config );
}
