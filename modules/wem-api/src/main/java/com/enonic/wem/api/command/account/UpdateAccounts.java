package com.enonic.wem.api.command.account;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.command.Command;

public final class UpdateAccounts
    extends Command<Integer>
{
    private AccountSelector selector;

    private AccountEditor editor;

    public AccountSelector getSelector()
    {
        return this.selector;
    }

    public AccountEditor getEditor()
    {
        return this.editor;
    }

    public UpdateAccounts selector( final AccountSelector selector )
    {
        this.selector = selector;
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
        Preconditions.checkNotNull( this.selector, "Account selector cannot be null" );
        Preconditions.checkNotNull( this.editor, "Editor cannot be null" );
    }
}
