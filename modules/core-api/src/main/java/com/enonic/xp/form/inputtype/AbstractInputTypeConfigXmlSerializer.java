package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public interface AbstractInputTypeConfigXmlSerializer<T extends InputTypeConfig>
{
    T parseConfig( ApplicationKey currentApp, Element elem );
}
