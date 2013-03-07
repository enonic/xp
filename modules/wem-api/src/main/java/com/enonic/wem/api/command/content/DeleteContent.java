package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.DeleteContentResult;

public final class DeleteContent
    extends Command<DeleteContentResult>
{
    private ContentSelector selector;

    private AccountKey deleter;

    public ContentSelector getSelector()
    {
        return this.selector;
    }

    public AccountKey getDeleter()
    {
        return deleter;
    }

    public DeleteContent selector( final ContentSelector selector )
    {
        this.selector = selector;
        return this;
    }

    public DeleteContent deleter( final AccountKey deleter )
    {
        this.deleter = deleter;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.selector, "Content selector cannot be null" );
    }
}
