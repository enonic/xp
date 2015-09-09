package com.enonic.xp.xml.parser;

import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.widget.WidgetDescriptor;
import com.enonic.xp.xml.DomElement;

@Beta
public final class XmlWidgetDescriptorParser
    extends XmlModelParser<XmlWidgetDescriptorParser>
{
    private WidgetDescriptor.Builder builder;

    public XmlWidgetDescriptorParser builder( final WidgetDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "widget" );
        this.builder.displayName( root.getChildValue( "display-name" ) );

        final DomElement interfaces = root.getChild( "interfaces" );
        if ( interfaces != null )
        {
            final List<DomElement> interfaceList = interfaces.getChildren( "interface" );
            for ( DomElement anInterface : interfaceList )
            {
                this.builder.addInterface( anInterface.getValue() );
            }
        }
    }
}
