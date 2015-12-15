package com.enonic.xp.admin.impl.adminapp;

import java.util.List;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

public class XmlAdminApplicationDescriptorParser
    extends XmlModelParser<XmlAdminApplicationDescriptorParser>
{
    private AdminApplicationDescriptor.Builder builder;

    public XmlAdminApplicationDescriptorParser builder( final AdminApplicationDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "admin-app" );
        this.builder.displayName( root.getChildValue( "display-name" ) );
        this.builder.icon( root.getChildValue( "icon" ) );

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
