package com.enonic.wem.api.content;


import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.attachment.Attachments;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class CreateContentParams2
{
    private Form form;

    private ContentData contentData;

    private ContentTypeName contentType;

    private UserKey owner;

    private String displayName;

    private ContentName name;

    private ContentPath parentContentPath;

    private boolean draft;

    private boolean embed;

    private Map<String, Attachment> attachments = Maps.newHashMap();

    public CreateContentParams2 contentType( final ContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public CreateContentParams2 parent( final ContentPath parentContentPath )
    {
        this.parentContentPath = parentContentPath;
        return this;
    }

    public CreateContentParams2 embed( final boolean value )
    {
        this.embed = value;
        return this;
    }

    public CreateContentParams2 form( final Form value )
    {
        this.form = value;
        return this;
    }

    public CreateContentParams2 contentData( final ContentData value )
    {
        this.contentData = value;
        return this;
    }

    public CreateContentParams2 owner( final UserKey owner )
    {
        this.owner = owner;
        return this;
    }

    public CreateContentParams2 displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public CreateContentParams2 name( final String name )
    {
        this.name = ContentName.from( name );
        return this;
    }

    public CreateContentParams2 name( final ContentName name )
    {
        this.name = name;
        return this;
    }

    public CreateContentParams2 draft()
    {
        this.draft = true;
        return this;
    }

    public CreateContentParams2 draft( final boolean value )
    {
        this.draft = value;
        return this;
    }

    public CreateContentParams2 attachments( final Attachment... attachments )
    {
        for ( Attachment attachment : attachments )
        {
            if ( attachment == null )
            {
                continue;
            }

            Preconditions.checkArgument( !this.attachments.containsKey( attachment.getName() ), "attachment with duplicated name: %s",
                                         attachment.getName() );
            this.attachments.put( attachment.getName(), attachment );
        }
        return this;
    }

    public CreateContentParams2 attachments( final Iterable<Attachment> attachments )
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

    public ContentName getName()
    {
        return name;
    }

    public boolean isDraft()
    {
        return draft;
    }

    public boolean isEmbed()
    {
        return embed;
    }

    public Attachments getAttachments()
    {
        return Attachments.from( attachments.values() );
    }

    public Attachment getAttachment( final String attachmentName )
    {
        return attachments.get( attachmentName );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentData, "contentData cannot be null" );
        Preconditions.checkArgument( draft || this.parentContentPath != null, "parentContentPath cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}
