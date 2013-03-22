package com.enonic.wem.core.content.relationship;


import javax.jcr.Session;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;

public class SyncRelationshipsCommand
{
    private Client client;

    private Session jcrSession;

    private QualifiedContentTypeName contentType;

    private ContentId contentToUpdate;

    private RootDataSet contentBeforeEditing;

    private RootDataSet contentAfterEditing;

    public SyncRelationshipsCommand client( final Client value )
    {
        this.client = value;
        return this;
    }

    public SyncRelationshipsCommand jcrSession( final Session value )
    {
        this.jcrSession = value;
        return this;
    }

    public SyncRelationshipsCommand contentType( final QualifiedContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public SyncRelationshipsCommand contentToUpdate( final ContentId value )
    {
        this.contentToUpdate = value;
        return this;
    }

    public SyncRelationshipsCommand contentBeforeEditing( final RootDataSet value )
    {
        this.contentBeforeEditing = value;
        return this;
    }

    public SyncRelationshipsCommand contentAfterEditing( final RootDataSet value )
    {
        this.contentAfterEditing = value;
        return this;
    }

    public Client getClient()
    {
        return client;
    }

    public Session getJcrSession()
    {
        return jcrSession;
    }

    public QualifiedContentTypeName getContentType()
    {
        return contentType;
    }

    public ContentId getContentToUpdate()
    {
        return contentToUpdate;
    }

    public RootDataSet getContentBeforeEditing()
    {
        return contentBeforeEditing;
    }

    public RootDataSet getContentAfterEditing()
    {
        return contentAfterEditing;
    }
}
