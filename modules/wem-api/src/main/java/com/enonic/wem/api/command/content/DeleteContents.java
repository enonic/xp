package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentDeletionResult;
import com.enonic.wem.api.content.ContentSelectors;

public final class DeleteContents
    extends Command<ContentDeletionResult>
{
    private ContentSelectors selectors;

    private AccountKey deleter;

    public ContentSelectors getSelectors()
    {
        return this.selectors;
    }

    public AccountKey getDeleter()
    {
        return deleter;
    }

    public DeleteContents selectors( final ContentSelectors selectors )
    {
        this.selectors = selectors;
        return this;
    }

    public DeleteContents deleter( final AccountKey deleter )
    {
        this.deleter = deleter;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selectors, "Content selectors cannot be null" );
    }
}
