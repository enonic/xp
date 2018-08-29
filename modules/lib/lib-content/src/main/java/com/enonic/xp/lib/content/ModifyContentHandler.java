package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.script.ScriptValue;

public final class ModifyContentHandler
    extends BaseContextHandler
{
    private String key;

    private ScriptValue editor;

    private boolean requireValid = true;

    @Override
    protected Object doExecute()
    {
        final Content existingContent = getExistingContent( this.key );
        if ( existingContent == null )
        {
            return null;
        }

        final UpdateContentParams params = new UpdateContentParams();
        params.contentId( existingContent.getId() );
        params.editor( newContentEditor( existingContent ) );
        params.requireValid( this.requireValid );

        final Content result = this.contentService.update( params );
        return result != null ? new ContentMapper( result ) : null;
    }

    private Content getExistingContent( final String key )
    {
        try
        {
            if ( !key.startsWith( "/" ) )
            {
                return this.contentService.getById( ContentId.from( key ) );
            }
            else
            {
                return this.contentService.getByPath( ContentPath.from( key ) );
            }
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private ContentEditor newContentEditor( final Content existingContent )
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new ContentMapper( edit.source ) );
            if ( value != null )
            {
                updateContent( edit, value.getMap(), existingContent );
            }
        };
    }

    private void updateContent( final EditableContent target, final Map<?, ?> map, final Content existingContent )
    {
        final String displayName = Converters.convert( map.get( "displayName" ), String.class );
        if ( displayName != null )
        {
            target.displayName = displayName;
        }

        final String languageCode = Converters.convert( map.get( "language" ), String.class );
        if ( languageCode != null )
        {
            target.language = Locale.forLanguageTag( languageCode );
        }

        final Object data = map.get( "data" );
        if ( data instanceof Map )
        {
            target.data = createPropertyTree( (Map) data, existingContent.getType() );
        }

        final Object extraData = map.get( "x" );
        if ( extraData instanceof Map )
        {
            target.extraDatas = createExtraDatas( (Map) extraData );
        }

        final Object publishInfo = map.get( "publish" );

        if ( publishInfo instanceof Map )
        {
            target.publishInfo = createContentPublishInfo( (Map) publishInfo );
        }
    }

    private ContentPublishInfo createContentPublishInfo( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        return ContentPublishInfo.create().
            from( getInstant( value, "from" ) ).
            to( getInstant( value, "to" ) ).
            build();
    }

    private Instant getInstant( final Map<String, Object> valueMap, final String key )
    {
        final Object value = valueMap.get( key );
        if ( value != null )
        {
            try
            {
                return Instant.parse( value.toString() );
            }
            catch ( DateTimeParseException e )
            {
                throw new IllegalArgumentException( key + " value could not be parsed to instant: [" + value + "]" );
            }
        }
        return null;
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), contentTypeName );
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final XDataName xDataName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), xDataName );
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
            final Object metadatasObject = value.get( applicationPrefix );
            if ( !( metadatasObject instanceof Map ) )
            {
                continue;
            }

            final Map<String, Object> metadatas = (Map<String, Object>) metadatasObject;

            for ( final String metadataName : metadatas.keySet() )
            {
                final XDataName xDataName = XDataName.from( applicationKey, metadataName );
                final ExtraData item = createExtraData( xDataName, metadatas.get( metadataName ) );
                if ( item != null )
                {
                    extradatasBuilder.add( item );
                }
            }
        }

        return extradatasBuilder.build();
    }


    private ExtraData createExtraData( final XDataName xDataName, final Object value )
    {
        if ( value instanceof Map )
        {
            final PropertyTree propertyTree = createPropertyTree( (Map) value, xDataName );

            if ( propertyTree != null )
            {
                return new ExtraData( xDataName, propertyTree );
            }
        }

        return null;
    }

    @Override
    protected boolean strictDataValidation()
    {
        return this.requireValid;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }

    public void setRequireValid( final Boolean requireValid )
    {
        if ( requireValid != null )
        {
            this.requireValid = requireValid;
        }
    }
}
