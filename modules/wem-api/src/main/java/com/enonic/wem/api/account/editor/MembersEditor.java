package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKeys;
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

    protected final AccountKeys keys;

    public MembersEditor( final Operation operation, final AccountKeys keys )
    {
        this.operation = operation;
        this.keys = keys;
    }

    @Override
    public boolean edit( final Account account )
        throws Exception
    {
        if ( account instanceof NonUserAccount )
        {
            return editNonUser( (NonUserAccount) account );
        }
        else
        {
            return false;
        }
    }

    private boolean editNonUser( final NonUserAccount account )
        throws Exception
    {
        final AccountKeys original = account.getMembers();

        if ( this.operation == Operation.SET )
        {
            account.setMembers( this.keys );
        }
        else if ( this.operation == Operation.ADD )
        {
            account.setMembers( original.add( this.keys ) );
        }
        else if ( this.operation == Operation.REMOVE )
        {
            account.setMembers( original.remove( this.keys ) );
        }

        return true;
    }
}
