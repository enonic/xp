package com.enonic.wem.api.command.content;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentPaths;

public final class DeleteContents
    extends Command
{
    private ContentPaths paths;

    private AccountKey deleter;

    public ContentPaths getPaths()
    {
        return this.paths;
    }

    public AccountKey getDeleter()
    {
        return deleter;
    }

    public DeleteContents paths( final ContentPaths paths )
    {
        this.paths = paths;
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
        Preconditions.checkNotNull( this.paths, "Content paths cannot be null" );
    }
}
