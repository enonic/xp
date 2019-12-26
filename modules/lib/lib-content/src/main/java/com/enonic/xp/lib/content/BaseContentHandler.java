package com.enonic.xp.lib.content;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.lib.common.FormJsonToPropertyTreeTranslator;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

public abstract class BaseContentHandler
    extends BaseContextHandler
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private XDataService xDataService;

    protected SiteService siteService;

    PropertyTree translateToPropertyTree( final JsonNode json, final ApplicationKey applicationKey, final ContentTypeName contentTypeName )
    {
        final SiteDescriptor siteDescriptor = this.siteService.getDescriptor( applicationKey );

        if ( siteDescriptor == null )
        {
            throw new IllegalArgumentException( "Site descriptor not found [" + applicationKey + "]" );
        }

        return new FormJsonToPropertyTreeTranslator( inlineMixins( siteDescriptor.getForm() ),
                                                     strictContentValidation( contentTypeName ) ).translate( json );
    }

    PropertyTree translateToPropertyTree( final JsonNode json, final XDataName xDataName, final ContentTypeName contentTypeName )
    {
        final XData xData = this.xDataService.getByName( xDataName );

        if ( xData == null )
        {
            throw new IllegalArgumentException( "XData not found [" + xDataName + "]" );
        }

        return new FormJsonToPropertyTreeTranslator( inlineMixins( xData.getForm() ),
                                                     strictContentValidation( contentTypeName ) ).translate( json );
    }

    PropertyTree translateToPropertyTree( final JsonNode json, final ContentTypeName contentTypeName )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) );

        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Content type not found [" + contentTypeName + "]" );
        }

        return new FormJsonToPropertyTreeTranslator( inlineMixins( contentType.getForm() ),
                                                     strictContentValidation( contentTypeName ) ).translate( json );
    }

    ExtraDatas createExtraDatas( final Map<String, Object> value, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        for ( final String applicationPrefix : value.keySet() )
        {
            final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( applicationPrefix );
            final Object metadatasObject = value.get( applicationPrefix );
            if ( !( metadatasObject instanceof Map ) )
            {
                continue;
            }

            final Map<String, Object> metadatas = (Map<String, Object>) metadatasObject;

            for ( final String metadataName : metadatas.keySet() )
            {
                final XDataName xDataName = XDataName.from( applicationKey, metadataName );
                final ExtraData item = createExtraData( xDataName, contentTypeName, metadatas.get( metadataName ) );
                if ( item != null )
                {
                    extradatasBuilder.add( item );
                }
            }
        }

        return extradatasBuilder.build();
    }


    private ExtraData createExtraData( final XDataName xDataName, final ContentTypeName contentTypeName, final Object value )
    {
        if ( value instanceof Map )
        {
            final PropertyTree propertyTree = createPropertyTree( (Map) value, xDataName, contentTypeName );

            if ( propertyTree != null )
            {
                return new ExtraData( xDataName, propertyTree );
            }
        }

        return null;
    }

    PropertyTree createPropertyTree( final Map<?, ?> value, final XDataName xDataName, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), xDataName, contentTypeName );
    }

    PropertyTree createPropertyTree( final Map<String, Object> value, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        final ObjectNode root = createJson( value );

        if ( root.has( ContentPropertyNames.SITECONFIG ) )
        {
            root.remove( ContentPropertyNames.SITECONFIG );

            final PropertyTree result = this.translateToPropertyTree( root, contentTypeName );
            final List<PropertySet> siteConfigs = createSiteConfigSets( value.get( ContentPropertyNames.SITECONFIG ), contentTypeName );

            if ( siteConfigs != null )
            {
                siteConfigs.forEach( propertySet -> {
                    final PropertySet newSet = propertySet.copy( result );
                    result.addSet( ContentPropertyNames.SITECONFIG, newSet );
                } );
            }
            return result;
        }

        return this.translateToPropertyTree( createJson( value ), contentTypeName );
    }

    List<PropertySet> createSiteConfigSets( final Object siteConfig, final ContentTypeName contentTypeName )
    {
        if ( siteConfig instanceof Map )
        {
            return List.of( createSitePropertySet( (Map) siteConfig, contentTypeName ) );
        }

        if ( siteConfig instanceof List )
        {
            return createSitePropertySets( (List) siteConfig, contentTypeName );
        }

        return null;
    }

    private List<PropertySet> createSitePropertySets( final List<Map<String, Object>> siteConfigs, final ContentTypeName contentTypeName )
    {
        if ( siteConfigs == null )
        {
            return null;
        }

        return siteConfigs.stream().
            map( siteConfig -> createSitePropertySet( siteConfig, contentTypeName ) ).
            collect( Collectors.toList() );
    }

    private PropertySet createSitePropertySet( final Map siteConfig, final ContentTypeName contentTypeName )
    {
        if ( siteConfig == null )
        {
            return null;
        }

        final ObjectNode appConfigNode = createJson( siteConfig );
        final ApplicationKey applicationKey = ApplicationKey.from( appConfigNode.get( "applicationKey" ).asText() );
        final ObjectNode appConfigData = (ObjectNode) appConfigNode.get( "config" );

        if ( appConfigData == null )
        {
            return null;
        }

        final PropertySet propertySet = new PropertySet();

        propertySet.addString( "applicationKey", applicationKey.toString() );
        propertySet.addSet( "config", this.translateToPropertyTree( appConfigData, applicationKey, contentTypeName ).getRoot() );

        return propertySet;
    }

    private Form inlineMixins( final Form form )
    {
        return this.mixinService.inlineFormItems( form );
    }

    boolean strictContentValidation( final ContentTypeName contentTypeName )
    {
        return !contentTypeName.isUnstructured() && strictDataValidation();
    }

    boolean strictDataValidation()
    {
        return true;
    }

    ObjectNode createJson( final Map<?, ?> value )
    {
        return MAPPER.valueToTree( value );
    }

    protected WorkflowInfo createWorkflowInfo( Map<String, Object> value )
    {
        if (value == null) {
            return null;
        }

        Object state = value.get( "state" );
        Object checks = value.get( "checks" );
        ImmutableMap.Builder<String, WorkflowCheckState> checkMapBuilder = ImmutableMap.builder();

        if (checks != null) {
            ((Map<String, String>) checks).entrySet().
                forEach( e -> checkMapBuilder.put(
                    e.getKey(),
                    WorkflowCheckState.valueOf( e.getValue() )
                         )
                );
        }

        return WorkflowInfo.create().
            state( state instanceof String ? (String) state : null ).
            checks( checkMapBuilder.build() ).
            build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
        this.contentTypeService = context.getService( ContentTypeService.class ).get();
        this.mixinService = context.getService( MixinService.class ).get();
        this.xDataService = context.getService( XDataService.class ).get();
        this.siteService = context.getService( SiteService.class ).get();
    }
}
