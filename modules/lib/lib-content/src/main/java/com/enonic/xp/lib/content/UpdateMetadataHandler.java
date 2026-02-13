package com.enonic.xp.lib.content;

import java.util.Locale;
import java.util.Map;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentMetadataEditor;
import com.enonic.xp.content.EditableContentMetadata;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentMetadataResult;
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
        final UpdateContentMetadataParams params =
            UpdateContentMetadataParams.create().contentId( getContentId( this.key ) ).editor( newContentMetadataEditor() ).build();

        final UpdateContentMetadataResult result = this.contentService.updateMetadata( params );

        return new UpdateContentMetadataResultMapper( result );
    }

    @Override
    protected boolean strictDataValidation()
    {
        return false;
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

    private void updateMetadata( final EditableContentMetadata target, final Map<String, ?> map )
    {
        edit( map, "language", String.class, val -> target.language = val.map( Locale::forLanguageTag ).orElse( null ) );
        edit( map, "owner", String.class, val -> target.owner = val.map( PrincipalKey::from ).orElse( null ) );
        edit( map, "variantOf", String.class, val -> target.variantOf = val.map( ContentId::from ).orElse( null ) );
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
