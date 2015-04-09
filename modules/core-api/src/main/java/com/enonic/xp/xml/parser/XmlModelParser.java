package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;

@Beta
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
