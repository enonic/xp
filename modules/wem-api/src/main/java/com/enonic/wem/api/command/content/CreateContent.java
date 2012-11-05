package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;

public final class CreateContent
    extends Command<ContentPath>
{
    private ContentPath contentPath;

    private ContentData contentData;

    private QualifiedContentTypeName contentType;

    private AccountKey owner;

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

    public CreateContent contentData( ContentData value )
    {
        this.contentData = value;
        return this;
    }

    public CreateContent owner( final AccountKey owner )
    {
        this.owner = owner;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentData, "contentData cannot be null" );
        Preconditions.checkNotNull( this.contentPath, "contentPath cannot be null" );
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public QualifiedContentTypeName getContentType()
    {
        return contentType;
    }

    public ContentData getContentData()
    {
        return contentData;
    }

    public AccountKey getOwner()
    {
        return owner;
    }
}
