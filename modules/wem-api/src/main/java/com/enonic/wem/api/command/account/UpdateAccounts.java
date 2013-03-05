package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.command.UpdateResult;

public final class UpdateAccounts
    extends Command<UpdateResult>
{
    private AccountKey key;

    private AccountEditor editor;

    public AccountKey getKey()
    {
        return this.key;
    }

    public AccountEditor getEditor()
    {
        return this.editor;
    }

    public UpdateAccounts key( final AccountKey key )
    {
        this.key = key;
        return this;
    }

    public UpdateAccounts editor( final AccountEditor editor )
    {
        this.editor = editor;
        return this;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.key, "Account key cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
