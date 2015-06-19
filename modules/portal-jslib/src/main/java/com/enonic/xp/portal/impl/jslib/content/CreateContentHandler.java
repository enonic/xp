package com.enonic.xp.portal.impl.jslib.content;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.impl.jslib.base.BaseContextHandler;
import com.enonic.xp.portal.impl.jslib.mapper.ContentMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

@Component(immediate = true, service = CommandHandler.class)
public final class CreateContentHandler
    extends BaseContextHandler
{
    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.create";
    }

    @Override
    protected Object doExecute( final CommandRequest req )
    {
        final CreateContentParams params = createParams( req );
        final Content result = this.contentService.create( params );
        return new ContentMapper( result );
    }

    private CreateContentParams createParams( final CommandRequest req )
    {
        final ContentTypeName contentTypeName = contentTypeName( req.param( "contentType" ).value( String.class ) );

        return CreateContentParams.create().
            name( req.param( "name" ).value( String.class ) ).
            parent( contentPath( req.param( "parentPath" ).value( String.class ) ) ).
            displayName( req.param( "displayName" ).value( String.class ) ).
            requireValid( req.param( "requireValid" ).value( Boolean.class, true ) ).
            type( contentTypeName ).
            contentData( createPropertyTree( req.param( "data" ).map(), contentTypeName ) ).
            extraDatas( createExtraDatas( req.param( "x" ).map() ) ).
            build();
    }

    private ContentPath contentPath( final String value )
    {
        return value != null ? ContentPath.from( value ) : null;
    }

    private ContentTypeName contentTypeName( final String value )
    {
        return value != null ? ContentTypeName.from( value ) : null;
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.contentService.translateToPropertyTree( createJson( value ), contentTypeName );
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final MixinName mixinName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.contentService.translateToPropertyTree( createJson( value ), mixinName );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    private ExtraDatas createExtraDatas( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        final ExtraDatas.Builder metadatasBuilder = ExtraDatas.create();
        for ( final String modulePrefix : value.keySet() )
        {
            final ModuleKey moduleKey = ExtraData.fromModulePrefix( modulePrefix );
            final Object metadatasObject = value.get( modulePrefix );
            if ( !( metadatasObject instanceof Map ) )
            {
                continue;
            }

            final Map<?, ?> metadatas = (Map<?, ?>) metadatasObject;
            for ( final Map.Entry<?, ?> entry : metadatas.entrySet() )
            {
                final MixinName mixinName = MixinName.from( moduleKey, entry.getKey().toString() );
                final ExtraData item = createExtraData( mixinName, entry.getValue() );
                if ( item != null )
                {
                    metadatasBuilder.add( item );
                }
            }
        }

        return metadatasBuilder.build();
    }

    private ExtraData createExtraData( final MixinName mixinName, final Object value )
    {
        if ( value instanceof Map )
        {
            return new ExtraData( mixinName, createPropertyTree( (Map) value, mixinName ) );
        }

        return null;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
