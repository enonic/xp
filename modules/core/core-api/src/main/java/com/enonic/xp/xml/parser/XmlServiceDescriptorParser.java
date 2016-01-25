package com.enonic.xp.xml.parser;

import java.util.LinkedList;
import java.util.List;

import com.google.common.annotations.Beta;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.service.ServiceDescriptor;
import com.enonic.xp.xml.DomElement;

@Beta
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
            final LinkedList<PrincipalKey> allowedPrincipalKeys = new LinkedList<>();
            final List<DomElement> allowedPrincipalList = allowedPrincipals.getChildren( "principal" );
            for ( DomElement allowedPrincipal : allowedPrincipalList )
            {
                allowedPrincipalKeys.add( PrincipalKey.from( allowedPrincipal.getValue() ) );
            }
            this.builder.setAllowedPrincipals( allowedPrincipalKeys );
        }
    }
}
