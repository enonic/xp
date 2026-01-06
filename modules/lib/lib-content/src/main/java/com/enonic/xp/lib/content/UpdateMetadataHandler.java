package com.enonic.xp.lib.content;

import java.util.Locale;
import java.util.Map;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentMetadataEditor;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.EditableContentMetadata;
import com.enonic.xp.content.UpdateMetadataParams;
import com.enonic.xp.content.UpdateMetadataResult;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.lib.content.mapper.UpdateMetadataResultMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public final class UpdateMetadataHandler
    extends BaseContentHandler
{
    private String key;

    private ScriptValue editor;

    @Override
    protected Object doExecute()
    {
        final Content existingContent = getExistingContent( this.key );
        if ( existingContent == null )
        {
            return null;
        }

        final UpdateMetadataParams params = UpdateMetadataParams.create()
            .contentId( existingContent.getId() )
            .editor( newContentMetadataEditor( existingContent ) )
            .build();

        final UpdateMetadataResult result = this.contentService.updateMetadata( params );

        return new UpdateMetadataResultMapper( result );
    }

    @Override
    protected boolean strictDataValidation()
    {
        return false;
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

    private ContentMetadataEditor newContentMetadataEditor( final Content existingContent )
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new ContentMapper( edit.source ) );
            if ( value != null )
            {
                updateMetadata( edit, value.getMap() );
            }
        };
    }

    private void updateMetadata( final EditableContentMetadata target, final Map<?, ?> map )
    {
        final String languageCode = Converters.convert( map.get( "language" ), String.class );
        if ( languageCode != null )
        {
            target.language = Locale.forLanguageTag( languageCode );
        }

        final String ownerKey = Converters.convert( map.get( "owner" ), String.class );
        if ( ownerKey != null )
        {
            target.owner = PrincipalKey.from( ownerKey );
        }

        final String variantOfId = Converters.convert( map.get( "variantOf" ), String.class );
        if ( variantOfId != null )
        {
            target.variantOf = ContentId.from( variantOfId );
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }
}
