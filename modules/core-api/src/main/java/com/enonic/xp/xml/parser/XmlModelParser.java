package com.enonic.xp.xml.parser;

import com.enonic.xp.module.ModuleKey;

public abstract class XmlModelParser<P extends XmlModelParser<P>>
    extends XmlObjectParser<P>
{
    protected ModuleKey currentModule;

    public final P currentModule( final ModuleKey value )
    {
        this.currentModule = value;
        return typecastThis();
    }
}
