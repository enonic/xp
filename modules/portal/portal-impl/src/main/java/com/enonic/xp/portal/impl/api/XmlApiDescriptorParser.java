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

    private static final String DISPLAY_NAME_TAG_NAME = "display-name";

    private static final String DESCRIPTION_TAG_NAME = "description";

    private static final String DOCUMENTATION_URL_TAG_NAME = "documentation-url";

    private static final String SLASH_API_TAG_NAME = "slash-api";

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

        this.builder.displayName( root.getChildValueTrimmed( DISPLAY_NAME_TAG_NAME ) );
        this.builder.description( root.getChildValue( DESCRIPTION_TAG_NAME ) );
        this.builder.documentationUrl( root.getChildValueTrimmed( DOCUMENTATION_URL_TAG_NAME ) );

        final String useInSlashApi = root.getChildValueTrimmed( SLASH_API_TAG_NAME );
        if ( useInSlashApi != null )
        {
            builder.slashApi( Boolean.valueOf( useInSlashApi ) );
        }

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
