package com.enonic.wem.api.account.editor;

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
    public void edit( final EditableAccount account )
        throws Exception
    {
        if ( account instanceof NonUserAccount )
        {
            editNonUser( (EditableNonUserAccount) account );
        }
    }

    private void editNonUser( final EditableNonUserAccount account )
        throws Exception
    {
        final AccountKeySet original = account.getMembers();

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
    }
}
