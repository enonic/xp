package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.NonUserAccount;

final class MembersEditor
    implements AccountEditor
{
    public enum Operation
    {
        SET,
        ADD,
        REMOVE
    }

    protected final Operation operation;

    protected final AccountKeySet keys;

    public MembersEditor( final Operation operation, final AccountKeySet keys )
    {
        this.operation = operation;
        this.keys = keys;
    }

    @Override
    public boolean edit( final Account account )
        throws Exception
    {
        return ( account instanceof NonUserAccount ) && editNonUser( (NonUserAccount) account );
    }

    private boolean editNonUser( final NonUserAccount account )
        throws Exception
    {
        final AccountKeySet original = account.getMembers();

        if ( this.operation == Operation.SET )
        {
            account.members( this.keys );
        }
        else if ( this.operation == Operation.ADD )
        {
            account.members( original.add( this.keys ) );
        }
        else if ( this.operation == Operation.REMOVE )
        {
            account.members( original.remove( this.keys ) );
        }

        return true;
    }
}
