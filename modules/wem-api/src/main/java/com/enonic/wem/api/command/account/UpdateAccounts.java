package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.command.Command;

public final class UpdateAccounts
    extends Command<Integer>
{
    private AccountKeys keys;

    private AccountEditor editor;

    public AccountKeys getKeys()
    {
        return this.keys;
    }

    public AccountEditor getEditor()
    {
        return this.editor;
    }

    public UpdateAccounts keys( final AccountKeys keys )
    {
        this.keys = keys;
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
        Preconditions.checkNotNull( this.keys, "Account keys cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
