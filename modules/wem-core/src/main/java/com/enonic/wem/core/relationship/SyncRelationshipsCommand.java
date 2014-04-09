package com.enonic.wem.core.relationship;


import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class SyncRelationshipsCommand
{
    private Session jcrSession;

    private ContentTypeName contentType;

    private ContentId contentToUpdate;

    private ContentData contentBeforeEditing;

    private ContentData contentAfterEditing;

    public SyncRelationshipsCommand jcrSession( final Session value )
    {
        this.jcrSession = value;
        return this;
    }

    public SyncRelationshipsCommand contentType( final ContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public SyncRelationshipsCommand contentToUpdate( final ContentId value )
    {
        this.contentToUpdate = value;
        return this;
    }

    public SyncRelationshipsCommand contentBeforeEditing( final ContentData value )
    {
        this.contentBeforeEditing = value;
        return this;
    }

    public SyncRelationshipsCommand contentAfterEditing( final ContentData value )
    {
        this.contentAfterEditing = value;
        return this;
    }

    public Session getJcrSession()
    {
        return jcrSession;
    }

    public ContentTypeName getContentType()
    {
        return contentType;
    }

    public ContentId getContentToUpdate()
    {
        return contentToUpdate;
    }

    public ContentData getContentBeforeEditing()
    {
        return contentBeforeEditing;
    }

    public ContentData getContentAfterEditing()
    {
        return contentAfterEditing;
    }
}
