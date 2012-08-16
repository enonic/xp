package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.GroupAccount;

public abstract class AccountEditorAdapter
    implements AccountEditor
{
    @Override
    public final void edit( final EditableAccount account )
        throws Exception
    {
        if ( account instanceof EditableUserAccount )
        {
            this.editUser( (EditableUserAccount) account );
        }
        else if ( account instanceof GroupAccount )
        {
            editGroup( (EditableGroupAccount) account );
        }
        else if ( account instanceof EditableRoleAccount )
        {
            editRole( (EditableRoleAccount) account );
        }
    }

    protected abstract void editUser( final EditableUserAccount account )
        throws Exception;

    protected abstract void editGroup( final EditableGroupAccount account )
        throws Exception;

    protected abstract void editRole( final EditableRoleAccount account )
        throws Exception;
}
