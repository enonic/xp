package com.enonic.xp.xml.parser;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.xml.DomElement;

@PublicApi
public final class XmlServiceDescriptorParser
    extends XmlModelParser<XmlServiceDescriptorParser>
{
    private ServiceDescriptor.Builder builder;

    public XmlServiceDescriptorParser builder( final ServiceDescriptor.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "service" );

        final DomElement allowedPrincipals = root.getChild( "allow" );
        if ( allowedPrincipals != null )
        {
            final List<PrincipalKey> allowedPrincipalKeys = new ArrayList<>();
            final List<DomElement> allowedPrincipalList = allowedPrincipals.getChildren( "principal" );
            for ( DomElement allowedPrincipal : allowedPrincipalList )
            {
                allowedPrincipalKeys.add( PrincipalKey.from( allowedPrincipal.getValue() ) );
            }
            this.builder.setAllowedPrincipals( allowedPrincipalKeys );
        }
    }
}
