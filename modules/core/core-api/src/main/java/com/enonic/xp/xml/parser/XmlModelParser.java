package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public abstract class XmlModelParser<P extends XmlModelParser<P>>
    extends XmlObjectParser<P>
{
    protected ApplicationKey currentApplication;

    public P currentApplication( final ApplicationKey value )
    {
        this.currentApplication = value;
        return typecastThis();
    }
}
