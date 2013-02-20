package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;

public final class CreateContent
    extends Command<ContentId>
{
    private ContentPath contentPath;

    private RootDataSet rootDataSet;

    private QualifiedContentTypeName contentType;

    private UserKey owner;

    private String displayName;

    public CreateContent contentPath( ContentPath value )
    {
        this.contentPath = value;
        return this;
    }

    public CreateContent contentType( QualifiedContentTypeName value )
    {
        this.contentType = value;
        return this;
    }

    public CreateContent rootDataSet( RootDataSet value )
    {
        this.rootDataSet = value;
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

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.rootDataSet, "rootDataSet cannot be null" );
        Preconditions.checkNotNull( this.contentPath, "contentPath cannot be null" );
        Preconditions.checkArgument( this.contentPath.isAbsolute(), "contentPath must be an absolute path and include the space" );
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public QualifiedContentTypeName getContentType()
    {
        return contentType;
    }

    public RootDataSet getRootDataSet()
    {
        return rootDataSet;
    }

    public UserKey getOwner()
    {
        return owner;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
