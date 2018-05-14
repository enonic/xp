package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlXDataParser
    extends XmlMixinParser<XmlXDataParser>
{
    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        super.doParse( root );

        root.getChildren( "allowContentType" ).forEach( domElement -> this.builder.allowContentType( domElement.getValue() ) );
    }
}
