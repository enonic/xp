package com.enonic.xp.lib.content;

import java.util.Map;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
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

    private void updateContent( final EditableContent target, final Map<String, ?> map, final Content existingContent )
    {
        edit( map, "displayName", String.class, val -> target.displayName = val.orElse( null ) );
        final String displayName = Converters.convert( map.get( "displayName" ), String.class );
        if ( displayName != null )
        {
            target.displayName = displayName;
        }

        final Object data = map.get( "data" );
        if ( data instanceof Map )
        {
            target.data = createPropertyTree( (Map) data, existingContent.getType() );
        }

        final Object extraData = map.get( "x" );
        if ( extraData instanceof Map )
        {
            target.extraDatas = createExtraDatas( (Map) extraData, existingContent.getType() );
        }

        updatePage( target, map );
    }

    private void updatePage( final EditableContent target, final Map<String, ?> map )
    {
        if ( map.containsKey( "page" ) )
        {
            final Map<String, ?> pageMap = (Map<String, ?>) map.get( "page" );
            if ( pageMap == null )
            {
                target.withoutPage();
            }
            else
            {
                edit( pageMap, "descriptor", String.class,
                       val -> target.page().descriptor = val.map( DescriptorKey::from ).orElse( null ) );
                edit( pageMap, "template", String.class, val -> target.page().template = val.map( PageTemplateKey::from ).orElse( null ) );
                edit( pageMap, "regions", Map.class, val -> target.page().regions = val.map( this::createRegions ).orElse( null ) );
                edit( pageMap, "fragment", Map.class, val -> target.page().fragment = val.map( this::createComponent ).orElse( null ) );
                edit( pageMap, "config", Map.class, val -> target.page().config =
                    val.map( dataMap -> createPropertyTree( dataMap, target.source.getType() ) ).orElse( null ) );
            }
        }
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
