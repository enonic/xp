package com.enonic.wem.api.command.content;


import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class CreateContent
    extends Command<CreateContentResult>
{
    public static final String THUMBNAIL_NAME = "_thumb.png";

    private Form form;

    private ContentData contentData;

    private ContentTypeName contentType;

    private UserKey owner;

    private String displayName;

    private String name;

    private ContentPath parentContentPath;

    private boolean draft;

    private Map<String, Attachment> attachments = Maps.newHashMap();

    public CreateContent contentType( final ContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public CreateContent parent( final ContentPath parentContentPath )
    {
        this.parentContentPath = parentContentPath;
        return this;
    }

    public CreateContent form( final Form value )
    {
        this.form = value;
        return this;
    }

    public CreateContent contentData( final ContentData value )
    {
        this.contentData = value;
        return this;
    }

    public CreateContent owner( final UserKey owner )
    {
        this.owner = owner;
        return this;
    }

    public CreateContent displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateContent name( final String name )
    {
        this.name = name;
        return this;
    }

    public CreateContent draft()
    {
        this.draft = true;
        return this;
    }

    public CreateContent draft( final boolean value )
    {
        this.draft = value;
        return this;
    }

    public CreateContent attachments( final Attachment... attachments )
    {
        for ( Attachment attachment : attachments )
        {
            Preconditions.checkArgument( !this.attachments.containsKey( attachment.getName() ), "attachment with duplicated name: %s",
                                         attachment.getName() );
            this.attachments.put( attachment.getName(), attachment );
        }
        return this;
    }

    public CreateContent attachments( final Iterable<Attachment> attachments )
    {
        return attachments( Iterables.toArray( attachments, Attachment.class ) );
    }

    public ContentPath getParentContentPath()
    {
        return parentContentPath;
    }

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public Form getForm()
    {
        return form;
    }

    public ContentData getContentData()
    {
        return contentData;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getName()
    {
        return name;
    }

    public boolean isDraft()
    {
        return draft;
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
        Preconditions.checkNotNull( this.contentData, "contentData cannot be null" );
        Preconditions.checkNotNull( this.form, "form cannot be null" );
        Preconditions.checkArgument( draft || this.parentContentPath != null, "parentContentPath cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
