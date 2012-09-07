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
    public boolean edit( final Account account )
        throws Exception
    {
        return edit( this.source, account );
    }

    private static boolean edit( final Account source, final Account target )
        throws Exception
    {
        if ( ( source instanceof UserAccount ) && ( target instanceof UserAccount ) )
        {
            return editUser( (UserAccount) source, (UserAccount) target );
        }
        else if ( ( source instanceof GroupAccount ) && ( target instanceof GroupAccount ) )
        {
            return editNonUser( (GroupAccount) source, (GroupAccount) target );
        }
        else if ( ( source instanceof RoleAccount ) && ( target instanceof RoleAccount ) )
        {
            return editNonUser( (RoleAccount) source, (RoleAccount) target );
        }
        else
        {
            return false;
        }
    }

    private static boolean editUser( final UserAccount source, final UserAccount target )
        throws Exception
    {
        target.setDisplayName( source.getDisplayName() );
        target.setEmail( source.getEmail() );
        target.setImage( source.getImage() );
        return true;
    }

    private static boolean editNonUser( final NonUserAccount source, final NonUserAccount target )
        throws Exception
    {
        target.setDisplayName( source.getDisplayName() );
        target.setMembers( source.getMembers() );
        return true;
    }
}
