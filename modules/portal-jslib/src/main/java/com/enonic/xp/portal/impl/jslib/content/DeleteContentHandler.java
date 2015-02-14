package com.enonic.xp.portal.impl.jslib.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class DeleteContentHandler
    implements CommandHandler
{
    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.delete";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String key = req.param( "key" ).required().value( String.class );
        if ( key.startsWith( "/" ) )
        {
            return deleteByPath( ContentPath.from( key ) );
        }
        else
        {
            return deleteById( ContentId.from( key ) );
        }
    }

    private boolean deleteById( final ContentId id )
    {
        try
        {
            final Content content = this.contentService.getById( id );
            return deleteByPath( content.getPath() );
        }
        catch ( final ContentNotFoundException e )
        {
            return false;
        }
    }

    private boolean deleteByPath( final ContentPath path )
    {
        final DeleteContentParams params = DeleteContentParams.create().
            contentPath( path ).
            build();
        return doDelete( params );
    }

    private boolean doDelete( final DeleteContentParams params )
    {
        try
        {
            return this.contentService.delete( params ) != null;
        }
        catch ( final ContentNotFoundException e )
        {
            return false;
        }
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
