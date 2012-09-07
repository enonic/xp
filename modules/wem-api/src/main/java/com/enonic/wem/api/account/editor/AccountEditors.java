package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKeys;

public abstract class AccountEditors
{
    public static AccountEditor addMembers( final AccountKeys keys )
    {
        return new MembersEditor( MembersEditor.Operation.ADD, keys );
    }

    public static AccountEditor removeMembers( final AccountKeys keys )
    {
        return new MembersEditor( MembersEditor.Operation.REMOVE, keys );
    }

    public static AccountEditor setMembers( final AccountKeys keys )
    {
        return new MembersEditor( MembersEditor.Operation.SET, keys );
    }

    public static AccountEditor composite( final AccountEditor... editors )
    {
        return new CompositeEditor( editors );
    }

    public static AccountEditor setAccount( final Account account )
    {
        return new SetAccountEditor( account );
    }
}
