package com.enonic.xp.xml.parser;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
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
