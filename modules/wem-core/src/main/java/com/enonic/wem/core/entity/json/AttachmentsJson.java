package com.enonic.wem.core.entity.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import com.enonic.wem.api.entity.Attachment;
import com.enonic.wem.api.entity.Attachments;

public class AttachmentsJson
{
    protected final List<AttachmentJson> attachmentsList;

    private final Attachments attachments;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    public AttachmentsJson( @JsonProperty("attachmentsList") final List<AttachmentJson> attachmentList )
    {
        this.attachmentsList = attachmentList;

        if ( attachmentList != null )
        {
            this.attachments = buildAttachmentsFromList( attachmentList );
        }
        else
        {
            attachments = null;
        }
    }

    private Attachments buildAttachmentsFromList( final List<AttachmentJson> attachmentList )
    {
        final Attachments.Builder attachmentsBuilder = Attachments.builder();

        for ( final AttachmentJson attachmentJson : attachmentList )
        {
            attachmentsBuilder.add( attachmentJson.getAttachment() );
        }

        return attachmentsBuilder.build();
    }

    public AttachmentsJson( final Attachments attachments )
    {
        this.attachments = attachments;
        this.attachmentsList = createAttachmentJsonList( attachments );
    }

    private List<AttachmentJson> createAttachmentJsonList( final Attachments attachments )
    {
        if ( attachments == null )
        {
            return null;
        }

        List<AttachmentJson> attachmentJsons = Lists.newArrayList();

        for ( final Attachment attachment : attachments )
        {
            attachmentJsons.add( new AttachmentJson( attachment ) );
        }

        return attachmentJsons;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<AttachmentJson> getAttachmentsList()
    {
        return attachmentsList;
    }

    @JsonIgnore
    public Attachments getAttachments()
    {
        return attachments;
    }
}



