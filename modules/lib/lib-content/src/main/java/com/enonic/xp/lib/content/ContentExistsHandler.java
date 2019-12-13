package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

import static com.google.common.base.Strings.nullToEmpty;

public final class ContentExistsHandler
    implements ScriptBean
{
    private ContentService contentService;

    private String key;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public boolean execute()
    {
        return doExecute();
    }

    private boolean doExecute()
    {
        if ( nullToEmpty( this.key ).isBlank() )
        {
            throw new IllegalArgumentException( "'key' param is empty" );
        }

        if ( this.key.startsWith( "/" ) )
        {
            return contentService.contentExists( ContentPath.from( this.key ) );
        }
        else
        {
            return contentService.contentExists( ContentId.from( this.key ) );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }
}
