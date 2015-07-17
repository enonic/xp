package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public abstract class XmlModelParser<P extends XmlModelParser<P>>
    extends XmlObjectParser<P>
{
    protected ApplicationKey currentModule;

    public final P currentModule( final ApplicationKey value )
    {
        this.currentModule = value;
        return typecastThis();
    }
}
