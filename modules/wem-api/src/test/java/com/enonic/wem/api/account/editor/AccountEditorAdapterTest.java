package com.enonic.wem.api.account.editor;

import org.junit.Test;
import org.mockito.Mockito;

public class AccountEditorAdapterTest
{
    @Test
    public void testUser()
        throws Exception
    {
        final EditableUserAccount account = Mockito.mock( EditableUserAccount.class );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editRole( Mockito.any( EditableRoleAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editGroup( Mockito.any( EditableGroupAccount.class ) );
        Mockito.verify( editor, Mockito.times( 1 ) ).editUser( account );
    }

    @Test
    public void testRole()
        throws Exception
    {
        final EditableRoleAccount account = Mockito.mock( EditableRoleAccount.class );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editUser( Mockito.any( EditableUserAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editGroup( Mockito.any( EditableGroupAccount.class ) );
        Mockito.verify( editor, Mockito.times( 1 ) ).editRole( account );
    }

    @Test
    public void testGroup()
        throws Exception
    {
        final EditableGroupAccount account = Mockito.mock( EditableGroupAccount.class );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editUser( Mockito.any( EditableUserAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editRole( Mockito.any( EditableRoleAccount.class ) );
        Mockito.verify( editor, Mockito.times( 1 ) ).editGroup( account );
    }

    @Test
    public void testNone()
        throws Exception
    {
        final EditableAccount account = Mockito.mock( EditableAccount.class );
        final AccountEditorAdapter editor = Mockito.mock( AccountEditorAdapter.class );

        editor.edit( account );

        Mockito.verify( editor, Mockito.times( 0 ) ).editUser( Mockito.any( EditableUserAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editRole( Mockito.any( EditableRoleAccount.class ) );
        Mockito.verify( editor, Mockito.times( 0 ) ).editGroup( Mockito.any( EditableGroupAccount.class ) );
    }
}
