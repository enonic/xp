package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.AccountKeySet;

public final class AccountEditors
{
    public static AccountEditor addMembers( final AccountKeySet keys )
    {
        return null;
    }

    public static AccountEditor removeMembers( final AccountKeySet keys )
    {
        return null;
    }

    public static AccountEditor setMembers( final AccountKeySet keys )
    {
        return null;
    }

    public static AccountEditor composite( final AccountEditor... editors )
    {
        return new CompositeEditor( editors );
    }
}
