package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

public abstract class AccountEditorAdapter
    implements AccountEditor
{
    @Override
    public final void edit( final Account account )
        throws Exception
    {
        if ( account instanceof UserAccount )
        {
            this.editUser( (UserAccount) account );
        }
        else if ( account instanceof GroupAccount )
        {
            editGroup( (GroupAccount) account );
        }
        else if ( account instanceof RoleAccount )
        {
            editRole( (RoleAccount) account );
        }
    }

    protected abstract void editUser( final UserAccount account )
        throws Exception;

    protected abstract void editGroup( final GroupAccount account )
        throws Exception;

    protected abstract void editRole( final RoleAccount account )
        throws Exception;
}
