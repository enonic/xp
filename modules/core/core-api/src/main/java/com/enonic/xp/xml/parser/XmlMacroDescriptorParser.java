package com.enonic.xp.xml.parser;

import com.google.common.annotations.Beta;

import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlMacroDescriptorParser
    extends XmlModelParser<XmlContentTypeParser>
{
    private MacroDescriptor.Builder builder;

    public XmlModelParser builder( final MacroDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "macro" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.description( root.getChildValue( "description" ) );

        final XmlFormMapper mapper = new XmlFormMapper( this.currentApplication );
        this.builder.form( mapper.buildForm( root.getChild( "form" ) ) );
    }

}
