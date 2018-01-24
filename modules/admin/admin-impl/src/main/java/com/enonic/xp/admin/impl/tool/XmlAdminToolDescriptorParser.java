package com.enonic.xp.admin.impl.tool;

import java.util.List;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

public class XmlAdminToolDescriptorParser
    extends XmlModelParser<XmlAdminToolDescriptorParser>
{
    private AdminToolDescriptor.Builder builder;

    public XmlAdminToolDescriptorParser builder( final AdminToolDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "tool" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.displayNameI18nKey(
            root.getChild( "display-name" ) != null ? root.getChild( "display-name" ).getAttribute( "i18n" ) : null );

        this.builder.description( root.getChildValue( "description" ) );
        this.builder.descriptionI18nKey(
            root.getChild( "description" ) != null ? root.getChild( "description" ).getAttribute( "i18n" ) : null );


        final DomElement allowedPrincipals = root.getChild( "allow" );
        if ( allowedPrincipals != null )
        {
            final List<DomElement> allowedPrincipalList = allowedPrincipals.getChildren( "principal" );
            for ( DomElement allowedPrincipal : allowedPrincipalList )
            {
                this.builder.addAllowedPrincipals( PrincipalKey.from( allowedPrincipal.getValue() ) );
            }
        }
    }
}
