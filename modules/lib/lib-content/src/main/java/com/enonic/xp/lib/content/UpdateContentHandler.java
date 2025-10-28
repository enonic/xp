package com.enonic.xp.lib.content;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.script.ScriptValue;

public final class UpdateContentHandler
    extends BaseContentHandler
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
        return new ContentMapper( result );
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
            target.extraDatas = createMixins( (Map) extraData, existingContent.getType() );
        }

        updatePage( target, map );

        final Object publishInfo = map.get( "publish" );

        if ( publishInfo instanceof Map )
        {
            target.publishInfo = createContentPublishInfo( (Map) publishInfo );
        }

        final Object workflowInfo = map.get( "workflow" );
        if ( workflowInfo instanceof Map )
        {
            target.workflowInfo = createWorkflowInfo( (Map) workflowInfo );
        }
    }

    private void updatePage( final EditableContent target, final Map<?, ?> map )
    {
        final Object page = map.get( "page" );
        if ( page instanceof Map<?, ?> pageMap )
        {
            if ( pageMap.containsKey( "descriptor" ) )
            {
                target.page.descriptor =
                    Optional.ofNullable( (String) pageMap.get( "descriptor" ) ).map( DescriptorKey::from ).orElse( null );
            }
            if ( pageMap.containsKey( "template" ) )
            {
                target.page.template =
                    Optional.ofNullable( (String) pageMap.get( "template" ) ).map( PageTemplateKey::from ).orElse( null );
            }
            if ( pageMap.containsKey( "regions" ) )
            {
                target.page.regions =
                    Optional.ofNullable( (Map<String, Object>) pageMap.get( "regions" ) ).map( this::createRegions ).orElse( null );
            }
            if ( pageMap.containsKey( "fragment" ) )
            {
                target.page.fragment =
                    Optional.ofNullable( (Map<String, Object>) pageMap.get( "fragment" ) ).map( this::createComponent ).orElse( null );
            }
            if ( pageMap.containsKey( "config" ) )
            {
                target.page.config = Optional.ofNullable( (Map<String, Object>) pageMap.get( "config" ) )
                    .map( dataMap -> createPropertyTree( dataMap, target.source.getType() ) )
                    .orElse( null );
            }
        }
    }

    private ContentPublishInfo createContentPublishInfo( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        return ContentPublishInfo.create().from( getInstant( value, "from" ) ).to( getInstant( value, "to" ) ).build();
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
