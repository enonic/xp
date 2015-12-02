package com.enonic.xp.lib.content;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetAttachmentStreamHandler
    implements ScriptBean

{
    private ContentService contentService;

    private String key;

    private String name;

    public ByteSource getStream()
    {
        if ( this.key.startsWith( "/" ) )
        {
            return getByPath( ContentPath.from( this.key ) );
        }
        else
        {
            return getById( ContentId.from( this.key ) );
        }
    }

    private ByteSource getByPath( final ContentPath key )
    {
        try
        {
            return getAttachment( this.contentService.getByPath( key ), this.name );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private ByteSource getById( final ContentId key )
    {
        try
        {
            return getAttachment( this.contentService.getById( key ), this.name );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private ByteSource getAttachment( final Content content, final String attachmentName )
    {
        final Attachment attachment = content.getAttachments().byName( attachmentName );
        if ( attachment == null )
        {
            return null;
        }
        return this.contentService.getBinary( content.getId(), attachment.getBinaryReference() );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }

}
