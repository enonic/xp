package com.enonic.xp.admin.impl.widget;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

final class XmlWidgetDescriptorParser
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
        this.builder.description( root.getChildValue( "description" ) );

        final DomElement interfaces = root.getChild( "interfaces" );
        if ( interfaces != null )
        {
            final List<DomElement> interfaceList = interfaces.getChildren( "interface" );
            for ( DomElement anInterface : interfaceList )
            {
                this.builder.addInterface( anInterface.getValue() );
            }
        }

        final DomElement allowedPrincipals = root.getChild( "allow" );
        if ( allowedPrincipals != null )
        {
            final List<PrincipalKey> allowedPrincipalList = allowedPrincipals.getChildren( "principal" ).
                stream().
                map( allowedPrincipal -> PrincipalKey.from( allowedPrincipal.getValue() ) ).
                collect( Collectors.toList() );
            this.builder.setAllowedPrincipals(allowedPrincipalList);
        }

        final DomElement config = root.getChild( "config" );
        if ( config != null )
        {
            final List<DomElement> properties = config.getChildren( "property" );
            for ( DomElement property : properties )
            {
                this.builder.addProperty( property.getAttribute( "name" ), property.getAttribute( "value" ) );
            }
        }
    }
}
