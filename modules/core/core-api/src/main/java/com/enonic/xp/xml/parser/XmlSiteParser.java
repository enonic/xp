package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.filter.FilterDescriptor;
import com.enonic.xp.site.filter.FilterDescriptors;
import com.enonic.xp.site.filter.FilterType;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.mapping.ControllerMappingDescriptors;
import com.enonic.xp.xml.DomElement;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

public final class XmlSiteParser
    extends XmlModelParser<XmlSiteParser>
{
    private static final String ROOT_TAG_NAME = "site";

    private static final String CONFIG_TAG_NAME = "config";

    private static final String META_STEP_TAG_NAME = "x-data";

    private static final String FILTER_DESCRIPTORS_PARENT_TAG_NAME = "filters";

    private static final String FILTER_DESCRIPTOR_TAG_NAME = "response-filter";

    private static final String MAPPINGS_DESCRIPTOR_TAG_NAME = "mappings";

    private static final String MAPPING_DESCRIPTOR_TAG_NAME = "mapping";

    private static final String MIXIN_ATTRIBUTE_NAME = "mixin";

    private static final String FILTER_DESCRIPTOR_NAME_ATTRIBUTE = "name";

    private static final String FILTER_DESCRIPTOR_ORDER_ATTRIBUTE = "order";

    private static final String MAPPING_DESCRIPTOR_CONTROLLER_ATTRIBUTE = "controller";

    private static final String MAPPING_DESCRIPTOR_ORDER_ATTRIBUTE = "order";

    private static final String MAPPING_DESCRIPTOR_MATCH_TAG_NAME = "match";

    private static final String MAPPING_DESCRIPTOR_PATTERN_TAG_NAME = "pattern";

    private static final String MAPPING_DESCRIPTOR_INVERT_ATTRIBUTE = "invert";

    private SiteDescriptor.Builder siteDescriptorBuilder;

    public XmlSiteParser siteDescriptorBuilder( final SiteDescriptor.Builder siteDescriptorBuilder )
    {
        this.siteDescriptorBuilder = siteDescriptorBuilder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, ROOT_TAG_NAME );

        final XmlFormMapper formMapper = new XmlFormMapper( this.currentApplication );
        this.siteDescriptorBuilder.form( formMapper.buildForm( root.getChild( CONFIG_TAG_NAME ) ) );
        this.siteDescriptorBuilder.metaSteps( MixinNames.from( parseMetaSteps( root ) ) );
        this.siteDescriptorBuilder.filterDescriptors(
            FilterDescriptors.from( parseFilterDescriptors( root.getChild( FILTER_DESCRIPTORS_PARENT_TAG_NAME ) ) ) );
        this.siteDescriptorBuilder.mappingDescriptors(
            ControllerMappingDescriptors.from( parseMappingDescriptors( root.getChild( MAPPINGS_DESCRIPTOR_TAG_NAME ) ) ) );
    }

    private List<MixinName> parseMetaSteps( final DomElement root )
    {
        return root.getChildren( META_STEP_TAG_NAME ).stream().map( this::toMixinName ).collect( Collectors.toList() );
    }

    private List<FilterDescriptor> parseFilterDescriptors( final DomElement filterDescriptorsParent )
    {
        if ( filterDescriptorsParent != null )
        {
            return filterDescriptorsParent.getChildren( FILTER_DESCRIPTOR_TAG_NAME ).stream().
                map( this::toFilterDescriptor ).
                collect( Collectors.toList() );
        }
        return Collections.emptyList();
    }

    private List<ControllerMappingDescriptor> parseMappingDescriptors( final DomElement filterDescriptorsParent )
    {
        if ( filterDescriptorsParent != null )
        {
            return filterDescriptorsParent.getChildren( MAPPING_DESCRIPTOR_TAG_NAME ).stream().
                map( this::toMappingDescriptor ).
                collect( Collectors.toList() );
        }
        return Collections.emptyList();
    }

    private MixinName toMixinName( final DomElement metaStep )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( this.currentApplication );
        final String name = metaStep.getAttribute( MIXIN_ATTRIBUTE_NAME );
        return resolver.toMixinName( name );
    }

    private FilterDescriptor toFilterDescriptor( final DomElement filterElement )
    {
        final FilterDescriptor.Builder builder = FilterDescriptor.create();
        final String orderValue = filterElement.getAttribute( FILTER_DESCRIPTOR_ORDER_ATTRIBUTE );
        if ( isNotEmpty( orderValue ) )
        {
            builder.order( Integer.parseInt( orderValue ) );
        }
        builder.name( filterElement.getAttribute( FILTER_DESCRIPTOR_NAME_ATTRIBUTE ) );
        builder.type( FilterType.RESPONSE );
        builder.application( this.currentApplication );
        return builder.build();
    }

    private ControllerMappingDescriptor toMappingDescriptor( final DomElement mappingElement )
    {
        final ControllerMappingDescriptor.Builder builder = ControllerMappingDescriptor.create();
        final String controllerPath = mappingElement.getAttribute( MAPPING_DESCRIPTOR_CONTROLLER_ATTRIBUTE );
        builder.controller( ResourceKey.from( this.currentApplication, controllerPath ) );

        final String orderValue = mappingElement.getAttribute( MAPPING_DESCRIPTOR_ORDER_ATTRIBUTE );
        if ( isNotEmpty( orderValue ) )
        {
            builder.order( Integer.parseInt( orderValue ) );
        }

        final DomElement matchElement = mappingElement.getChild( MAPPING_DESCRIPTOR_MATCH_TAG_NAME );
        if ( matchElement != null )
        {
            final String match = matchElement.getValue();
            if ( isNotEmpty( match ) )
            {
                builder.contentConstraint( match );
            }
        }

        final DomElement patternElement = mappingElement.getChild( MAPPING_DESCRIPTOR_PATTERN_TAG_NAME );
        if ( patternElement != null )
        {
            final String pattern = patternElement.getValue();
            if ( isNotEmpty( pattern ) )
            {
                final boolean invert = "true".equals( patternElement.getAttribute( MAPPING_DESCRIPTOR_INVERT_ATTRIBUTE, "false" ) );
                builder.pattern( pattern );
                builder.invertPattern( invert );
            }
        }

        return builder.build();
    }
}
