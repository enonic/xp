package com.enonic.wem.api.account.editor;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

public class AccountEditorAdapterTest
{
    @Test
    public void testUser()
        throws Exception
    {
        final UserAccount account = UserAccount.create( "other:dummy" );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editRole( Mockito.any( RoleAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editGroup( Mockito.any( GroupAccount.class ) );
        Mockito.verify( editor, Mockito.times( 1 ) ).editUser( account );
    }

    @Test
    public void testRole()
        throws Exception
    {
        final RoleAccount account = RoleAccount.create( "other:dummy" );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editUser( Mockito.any( UserAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editGroup( Mockito.any( GroupAccount.class ) );
        Mockito.verify( editor, Mockito.times( 1 ) ).editRole( account );
    }

    @Test
    public void testGroup()
        throws Exception
    {
        final GroupAccount account = GroupAccount.create( "other:dummy" );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editUser( Mockito.any( UserAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editRole( Mockito.any( RoleAccount.class ) );
        Mockito.verify( editor, Mockito.times( 1 ) ).editGroup( account );
    }

    @Test
    public void testNone()
        throws Exception
    {
        final Account account = Mockito.mock( Account.class );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editUser( Mockito.any( UserAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editRole( Mockito.any( RoleAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editGroup( Mockito.any( GroupAccount.class ) );
    }
}
