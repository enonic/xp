package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

final class SetAccountEditor
    extends AccountEditorAdapter
{
    protected final Account account;

    public SetAccountEditor( final Account account )
    {
        this.account = account;
    }

    @Override
    protected boolean editUser( final UserAccount account )
        throws Exception
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected boolean editGroup( final GroupAccount account )
        throws Exception
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected boolean editRole( final RoleAccount account )
        throws Exception
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
