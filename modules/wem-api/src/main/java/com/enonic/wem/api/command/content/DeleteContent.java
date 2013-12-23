package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.DeleteContentResult;

public final class DeleteContent
    extends Command<DeleteContentResult>
{
    private ContentPath contentPath;

    private AccountKey deleter;

    public AccountKey getDeleter()
    {
        return deleter;
    }

    public DeleteContent contentPath( final ContentPath contentPath )
    {
        this.contentPath = contentPath;
        return this;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public DeleteContent deleter( final AccountKey deleter )
    {
        this.deleter = deleter;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.contentPath, "ContentPath cannot be null" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof DeleteContent ) )
        {
            return false;
        }

        final DeleteContent that = (DeleteContent) o;

        if ( !contentPath.equals( that.contentPath ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentPath.hashCode();
    }
}
