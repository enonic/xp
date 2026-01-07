package com.enonic.xp.lib.content;

import java.util.Locale;
import java.util.Map;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentMetadataEditor;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.EditableContentMetadata;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentMetadataResult;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.lib.content.mapper.UpdateContentMetadataResultMapper;
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

        final UpdateContentMetadataParams params =
            UpdateContentMetadataParams.create().contentId( existingContent.getId() ).editor( newContentMetadataEditor() ).build();

        final UpdateContentMetadataResult result = this.contentService.updateMetadata( params );

        return new UpdateContentMetadataResultMapper( result );
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

    private ContentMetadataEditor newContentMetadataEditor()
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
        if ( map.containsKey( "language" ) )
        {
            final String languageCode = Converters.convert( map.get( "language" ), String.class );
            target.language = languageCode != null ? Locale.forLanguageTag( languageCode ) : null;
        }

        if ( map.containsKey( "owner" ) )
        {
            final String ownerKey = Converters.convert( map.get( "owner" ), String.class );
            target.owner = ownerKey != null ? PrincipalKey.from( ownerKey ) : null;
        }

        if ( map.containsKey( "variantOf" ) )
        {
            final String variantOfId = Converters.convert( map.get( "variantOf" ), String.class );
            target.variantOf = variantOfId != null ? ContentId.from( variantOfId ) : null;
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
