package com.enonic.wem.api.command.content;


import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.editor.ContentEditor;

public final class UpdateContent
    extends Command<UpdateContentResult>
{
    private ContentEditor editor;

    private UserKey modifier;

    private ContentId contentId;

    private Map<String, Attachment> attachments = Maps.newHashMap();

    public ContentEditor getEditor()
    {
        return this.editor;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public UpdateContent editor( final ContentEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public UpdateContent contentId( final ContentId contentId )
    {
        this.contentId = contentId;
        return this;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public UpdateContent modifier( final UserKey modifier )
    {
        this.modifier = modifier;
        return this;
    }

    public UpdateContent attachments( final Attachment... attachments )
    {
        for ( Attachment attachment : attachments )
        {
            Preconditions.checkArgument( !this.attachments.containsKey( attachment.getName() ), "attachment with duplicated name: %s",
                                         attachment.getName() );
            this.attachments.put( attachment.getName(), attachment );
        }
        return this;
    }

    public UpdateContent attachments( final Iterable<Attachment> attachments )
    {
        return attachments( Iterables.toArray( attachments, Attachment.class ) );
    }

    public Collection<Attachment> getAttachments()
    {
        return attachments.values();
    }

    public Attachment getAttachment( final String attachmentName )
    {
        return attachments.get( attachmentName );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "contentId cannot be null" );
        Preconditions.checkNotNull( this.editor, "editor cannot be null" );
    }
}
