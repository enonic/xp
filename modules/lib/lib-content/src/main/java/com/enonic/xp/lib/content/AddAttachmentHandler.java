package com.enonic.xp.lib.content;

import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class AddAttachmentHandler
    implements ScriptBean
{
    private ContentService contentService;

    private String key;

    private String name;

    private String mimeType;

    private String label;

    private Object data;

    public void execute()
    {
        UpdateContentParams updateContent = new UpdateContentParams();
        if ( !this.key.startsWith( "/" ) )
        {
            updateContent.contentId( ContentId.from( this.key ) );
        }
        else
        {
            final Content contentByPath = this.contentService.getByPath( ContentPath.from( key ) );
            updateContent.contentId( contentByPath.getId() );
        }

        final CreateAttachment createAttachment = CreateAttachment.create().
            name( this.name ).
            label( this.label ).
            mimeType( this.mimeType ).
            byteSource( getData() ).
            build();
        updateContent.createAttachments( CreateAttachments.from( createAttachment ) );
        contentService.update( updateContent );
    }

    private ByteSource getData()
    {
        if ( data instanceof ByteSource )
        {
            return (ByteSource) data;
        }
        else if ( data instanceof String )
        {
            return ByteSource.wrap( ( (String) data ).getBytes( StandardCharsets.UTF_8 ) );
        }
        else
        {
            return ByteSource.wrap( ( data.toString() ).getBytes( StandardCharsets.UTF_8 ) );
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
    }

    public void setLabel( final String label )
    {
        this.label = label;
    }

    public void setData( final Object data )
    {
        this.data = data;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }

}
