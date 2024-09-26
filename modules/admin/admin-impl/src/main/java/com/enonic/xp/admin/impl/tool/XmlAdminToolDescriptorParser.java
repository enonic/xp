package com.enonic.xp.admin.impl.tool;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.api.ApiMountDescriptor;
import com.enonic.xp.api.ApiMountDescriptors;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

public class XmlAdminToolDescriptorParser
    extends XmlModelParser<XmlAdminToolDescriptorParser>
{
    private static final String APIS_DESCRIPTOR_TAG_NAME = "apis";

    private static final String API_DESCRIPTOR_TAG_NAME = "api";

    private static final String INTERFACES_DESCRIPTOR_TAG_NAME = "interfaces";

    private static final String INTERFACE_DESCRIPTOR_TAG_NAME = "interface";

    private static final int APPLICATION_KEY_INDEX = 0;

    private static final int API_KEY_INDEX = 1;

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
        this.builder.displayName( root.getChildValueTrimmed( "display-name" ) );
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
                this.builder.addAllowedPrincipals( PrincipalKey.from( allowedPrincipal.getValue().trim() ) );
            }
        }

        this.builder.apiMounts( ApiMountDescriptors.from( parseApiMounts( root.getChild( APIS_DESCRIPTOR_TAG_NAME ) ) ) );

        final DomElement interfaces = root.getChild( INTERFACES_DESCRIPTOR_TAG_NAME );
        if ( interfaces != null )
        {
            final List<DomElement> interfaceList = interfaces.getChildren( INTERFACE_DESCRIPTOR_TAG_NAME );
            for ( DomElement anInterface : interfaceList )
            {
                this.builder.addInterface( anInterface.getValue().trim() );
            }
        }
    }

    private List<ApiMountDescriptor> parseApiMounts( final DomElement apisElement )
    {
        if ( apisElement == null )
        {
            return Collections.emptyList();
        }

        return apisElement.getChildren( API_DESCRIPTOR_TAG_NAME ).stream().map( this::toApiMountDescriptor ).collect( Collectors.toList() );
    }

    private ApiMountDescriptor toApiMountDescriptor( final DomElement apiElement )
    {
        final ApiMountDescriptor.Builder builder = ApiMountDescriptor.create();

        final String apiMount = apiElement.getValue().trim();

        if ( !apiMount.contains( ":" ) )
        {
            builder.applicationKey( this.currentApplication );
            if ( !apiMount.isBlank() )
            {
                builder.apiKey( apiMount );
            }
        }
        else
        {
            final String[] parts = apiMount.split( ":", 2 );

            builder.applicationKey( resolveApplicationKey( parts[APPLICATION_KEY_INDEX].trim() ) );
            final String apiKey = parts[API_KEY_INDEX].trim();
            if ( !apiKey.isBlank() )
            {
                builder.apiKey( apiKey );
            }
        }

        return builder.build();
    }

    private ApplicationKey resolveApplicationKey( final String applicationKey )
    {
        try
        {
            return ApplicationKey.from( applicationKey );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( String.format( "Invalid applicationKey '%s'", applicationKey ), e );
        }
    }
}
