package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

final class SetAccountEditor
    implements AccountEditor
{
    protected final Account source;

    public SetAccountEditor( final Account source )
    {
        this.source = source;
    }

    @Override
    public void edit( final EditableAccount account )
        throws Exception
    {
        edit( this.source, account );
    }

    private static void edit( final Account source, final EditableAccount target )
        throws Exception
    {
        if ( ( source instanceof UserAccount ) && ( target instanceof EditableUserAccount ) )
        {
            editUser( (UserAccount) source, (EditableUserAccount) target );
        }
        else if ( ( source instanceof GroupAccount ) && ( target instanceof EditableGroupAccount ) )
        {
            editNonUser( (GroupAccount) source, (EditableGroupAccount) target );
        }
        else if ( ( source instanceof RoleAccount ) && ( target instanceof EditableRoleAccount ) )
        {
            editNonUser( (RoleAccount) source, (EditableRoleAccount) target );
        }
    }

    private static void editUser( final UserAccount source, final EditableUserAccount target )
        throws Exception
    {
        target.setDisplayName( source.getDisplayName() );
        target.setEmail( source.getEmail() );
        target.setPhoto( source.getPhoto() );
    }

    private static void editNonUser( final NonUserAccount source, final EditableNonUserAccount target )
        throws Exception
    {
        target.setDisplayName( source.getDisplayName() );
        target.setMembers( source.getMembers() );
    }
}
