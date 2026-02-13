package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class BaseContextHandler
    implements ScriptBean
{
    protected ContentService contentService;

    public final Object execute()
    {
        return this.doExecute();
    }

    protected abstract Object doExecute();

    protected ContentId getContentId( final String key )
    {
        if ( key == null || key.isEmpty() )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }
        if ( key.startsWith( "/" ) )
        {
            // source is path
            final Content sourceContent = contentService.getByPath( ContentPath.from( key ) );
            return sourceContent.getId();
        }
        else
        {
            // source is key
            return ContentId.from( key );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
