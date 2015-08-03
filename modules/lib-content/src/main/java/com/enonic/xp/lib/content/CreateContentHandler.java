package com.enonic.xp.lib.content;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;

public final class CreateContentHandler
    extends BaseContextHandler
{
    private String name;

    private String parentPath;

    private String displayName;

    private boolean requireValid = true;

    private Map<String, Object> data;

    private Map<String, Object> x;

    private String contentType;

    @Override
    protected Object doExecute()
    {
        final CreateContentParams params = createParams();
        final Content result = this.contentService.create( params );
        return new ContentMapper( result );
    }

    private CreateContentParams createParams()
    {
        final ContentTypeName contentTypeName = contentTypeName( this.contentType );

        return CreateContentParams.create().
            name( this.name ).
            parent( contentPath( this.parentPath ) ).
            displayName( this.displayName ).
            requireValid( this.requireValid ).
            type( contentTypeName ).
            contentData( createPropertyTree( data, contentTypeName ) ).
            extraDatas( createExtraDatas( x ) ).
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

        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        for ( final String applicationPrefix : value.keySet() )
        {
            final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( applicationPrefix );
            final Object extradatasObject = value.get( applicationPrefix );
            if ( !( extradatasObject instanceof Map ) )
            {
                continue;
            }

            final Map<?, ?> extradatas = (Map<?, ?>) extradatasObject;
            for ( final Map.Entry<?, ?> entry : extradatas.entrySet() )
            {
                final MixinName mixinName = MixinName.from( applicationKey, entry.getKey().toString() );
                final ExtraData item = createExtraData( mixinName, entry.getValue() );
                if ( item != null )
                {
                    extradatasBuilder.add( item );
                }
            }
        }

        return extradatasBuilder.build();
    }

    private ExtraData createExtraData( final MixinName mixinName, final Object value )
    {
        if ( value instanceof Map )
        {
            final PropertyTree tree = createPropertyTree( (Map) value, mixinName );
            return tree != null ? new ExtraData( mixinName, tree ) : null;
        }

        return null;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setRequireValid( final boolean requireValid )
    {
        this.requireValid = requireValid;
    }

    public void setData( final ScriptValue data )
    {
        this.data = data != null ? data.getMap() : null;
    }

    public void setX( final ScriptValue x )
    {
        this.x = x != null ? x.getMap() : null;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }
}
