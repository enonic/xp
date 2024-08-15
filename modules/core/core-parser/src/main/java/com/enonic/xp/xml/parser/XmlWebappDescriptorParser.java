package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.api.ApiMountDescriptor;
import com.enonic.xp.api.ApiMountDescriptors;
import com.enonic.xp.impl.common.ApiMountResolver;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.xml.DomElement;

@PublicApi
public final class XmlWebappDescriptorParser
    extends XmlModelParser<XmlWebappDescriptorParser>
{
    private static final String ROOT_TAG_NAME = "webapp";

    private static final String APIS_DESCRIPTOR_TAG_NAME = "apis";

    private static final String API_DESCRIPTOR_TAG_NAME = "api";

    private static final int APPLICATION_KEY_INDEX = 0;

    private static final int API_KEY_INDEX = 1;

    private WebappDescriptor.Builder descriptorBuilder;

    public XmlWebappDescriptorParser descriptorBuilder( final WebappDescriptor.Builder descriptorBuilder )
    {
        this.descriptorBuilder = descriptorBuilder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, ROOT_TAG_NAME );

        this.descriptorBuilder.applicationKey( this.currentApplication );
        this.descriptorBuilder.apiMounts( ApiMountDescriptors.from( parseApiMounts( root.getChild( APIS_DESCRIPTOR_TAG_NAME ) ) ) );
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
        final ApiMountResolver apiMountResolver = new ApiMountResolver( apiElement.getValue().trim() );

        return ApiMountDescriptor.create()
            .applicationKey( Objects.requireNonNullElse( apiMountResolver.resolveApplicationKey(), this.currentApplication ) )
            .apiKey( apiMountResolver.resolveApiKey() )
            .build();
    }
}
