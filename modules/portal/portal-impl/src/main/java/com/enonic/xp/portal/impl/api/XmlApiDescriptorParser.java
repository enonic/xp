package com.enonic.xp.portal.impl.api;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

public final class XmlApiDescriptorParser
    extends XmlModelParser<XmlApiDescriptorParser>
{
    private static final String ROOT_TAG_NAME = "api";

    private static final String ALLOW_TAG_NAME = "allow";

    private static final String PRINCIPAL_TAG_NAME = "principal";

    private final ApiDescriptor.Builder builder;

    public XmlApiDescriptorParser( final ApiDescriptor.Builder builder )
    {
        this.builder = builder;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, ROOT_TAG_NAME );

        DomElement allowedPrincipals = root.getChild( ALLOW_TAG_NAME );

        if ( allowedPrincipals != null )
        {
            List<PrincipalKey> allowedPrincipalKeys = allowedPrincipals.getChildren( PRINCIPAL_TAG_NAME )
                .stream()
                .map( allowedPrincipal -> PrincipalKey.from( allowedPrincipal.getValue().trim() ) )
                .collect( Collectors.toList() );
            this.builder.allowedPrincipals( PrincipalKeys.from( allowedPrincipalKeys ) );
        }
    }
}
