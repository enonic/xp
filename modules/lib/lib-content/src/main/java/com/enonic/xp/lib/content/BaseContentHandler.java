package com.enonic.xp.lib.content;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.CmsService;

public abstract class BaseContentHandler
    extends BaseContextHandler
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    protected CmsService cmsService;

    private Supplier<PropertyTreeMarshallerService> propertyTreeMarshallerService;

    private PropertyTree translateToPropertyTree( final Map<String, ?> json, final ApplicationKey applicationKey,
                                                  final ContentTypeName contentTypeName )
    {
        final CmsDescriptor siteDescriptor = this.cmsService.getDescriptor( applicationKey );

        if ( siteDescriptor == null )
        {
            throw new IllegalArgumentException( "Site descriptor not found [" + applicationKey + "]" );
        }

        return propertyTreeMarshallerService.get().marshal( json, siteDescriptor.getForm(), strictContentValidation( contentTypeName ) );
    }

    private PropertyTree translateToPropertyTree( final Map<String, ?> json, final XDataName xDataName,
                                                  final ContentTypeName contentTypeName )
    {
        final XData xData = this.xDataService.getByName( xDataName );

        if ( xData == null )
        {
            throw new IllegalArgumentException( "XData not found [" + xDataName + "]" );
        }

        return propertyTreeMarshallerService.get().marshal( json, xData.getForm(), strictContentValidation( contentTypeName ) );
    }

    private PropertyTree translateToPropertyTree( final Map<String, ?> json, final ContentTypeName contentTypeName )
    {
        final ContentType contentType = this.contentTypeService.getByName( GetContentTypeParams.from( contentTypeName ) );

        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Content type not found [" + contentTypeName + "]" );
        }

        return propertyTreeMarshallerService.get().marshal( json, contentType.getForm(), strictContentValidation( contentTypeName ) );
    }

    ExtraDatas createExtraDatas( final Map<String, Object> mapValue, final ContentTypeName contentTypeName )
    {
        if ( mapValue == null )
        {
            return null;
        }

        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        mapValue.forEach( ( key, metadatasObject ) -> {
            final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( key );

            if ( metadatasObject instanceof Map )
            {
                final Map<String, Object> metadatas = (Map<String, Object>) metadatasObject;

                metadatas.forEach( ( metadataName, value ) -> {
                    final XDataName xDataName = XDataName.from( applicationKey, metadataName );
                    final ExtraData item = createExtraData( xDataName, contentTypeName, value );
                    if ( item != null )
                    {
                        extradatasBuilder.add( item );
                    }
                } );
            }
        } );

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

    PropertyTree createPropertyTree( final Map<String, ?> value, final XDataName xDataName, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( value, xDataName, contentTypeName );
    }

    PropertyTree createPropertyTree( final Map<String, Object> value, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value.containsKey( ContentPropertyNames.SITECONFIG ) )
        {
            Map<String, Object> newValue = new LinkedHashMap<>( value );
            newValue.remove( ContentPropertyNames.SITECONFIG );

            final PropertyTree result = this.translateToPropertyTree( newValue, contentTypeName );
            addSiteConfigs( value.get( ContentPropertyNames.SITECONFIG ), contentTypeName, result.getRoot() );

            return result;
        }

        return this.translateToPropertyTree( value, contentTypeName );
    }

    private void addSiteConfigs( final Object siteConfig, final ContentTypeName contentTypeName, final PropertySet into )
    {
        if ( siteConfig instanceof Map )
        {
            addSiteConfig( (Map) siteConfig, contentTypeName, into );
        }

        if ( siteConfig instanceof List )
        {
            ( (List<Map<String, Object>>) siteConfig ).stream().forEach( sc -> addSiteConfig( sc, contentTypeName, into ) );
        }
    }

    private void addSiteConfig( final Map<String, Object> siteConfig, final ContentTypeName contentTypeName, final PropertySet into )
    {
        final ApplicationKey applicationKey = ApplicationKey.from( siteConfig.get( "applicationKey" ).toString() );
        final Map<String, ?> appConfigData = (Map<String, ?>) siteConfig.get( "config" );

        if ( appConfigData == null )
        {
            return;
        }

        final PropertySet propertySet = into.addSet( ContentPropertyNames.SITECONFIG );

        propertySet.addString( "applicationKey", applicationKey.toString() );
        propertySet.addSet( "config", this.translateToPropertyTree( appConfigData, applicationKey, contentTypeName )
            .getRoot()
            .copy( propertySet.getTree() ) );
    }

    boolean strictContentValidation( final ContentTypeName contentTypeName )
    {
        return !contentTypeName.isUnstructured() && strictDataValidation();
    }

    boolean strictDataValidation()
    {
        return true;
    }

    protected WorkflowInfo createWorkflowInfo( Map<String, Object> map )
    {
        if ( map == null )
        {
            return null;
        }

        Object state = map.get( "state" );
        Object checks = map.get( "checks" );

        Map<String, WorkflowCheckState> checkMapBuilder = new LinkedHashMap<>();

        if ( checks != null )
        {
            ( (Map<String, String>) checks ).forEach( ( key, value ) -> checkMapBuilder.put( key, WorkflowCheckState.valueOf( value ) ) );
        }

        return WorkflowInfo.create().state( state instanceof String ? (String) state : null ).checks( checkMapBuilder ).build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
        this.contentTypeService = context.getService( ContentTypeService.class ).get();
        this.xDataService = context.getService( XDataService.class ).get();
        this.cmsService = context.getService( CmsService.class ).get();
        this.propertyTreeMarshallerService = context.getService( PropertyTreeMarshallerService.class );
    }
}
