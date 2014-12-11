package com.enonic.wem.jsapi.internal.content;

import java.util.function.Function;

import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.command.CommandRequest;

// @Component(immediate = true)
public final class ModifyContentHandler
    implements CommandHandler
{
    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.modify";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String key = req.param( "key" ).required().value( String.class );
        final Function<Object[], Object> editor = req.param( "editor" ).required().callback();

        final ContentId id = findContentId( key );
        if ( id == null )
        {
            return null;
        }

        final UpdateContentParams params = new UpdateContentParams();
        params.contentId( id );
        //params.editor( newContentEditor( editor ) );

        this.contentService.update( params );
        return null;
    }

    private ContentId findContentId( final String key )
    {
        if ( !key.startsWith( "/" ) )
        {
            return ContentId.from( key );
        }

        try
        {
            final Content content = this.contentService.getByPath( ContentPath.from( key ) );
            return content.getId();
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    /*private ContentEditor newContentEditor( final Function<Object[], Object> func )
    {
        return toBeEdited -> {
            final Object value = func.apply( new Object[]{new ContentMapper( toBeEdited )} );
            return null;
        };
    }*/

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
