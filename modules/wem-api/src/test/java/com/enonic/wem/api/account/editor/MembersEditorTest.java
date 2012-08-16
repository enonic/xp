package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKeySet;

public class MembersEditorTest
{
    private EditableUserAccount user;

    private EditableGroupAccount group;

    private EditableRoleAccount role;

    @Before
    public void setUp()
    {
        this.user = Mockito.mock( EditableUserAccount.class );
        this.group = Mockito.mock( EditableGroupAccount.class );
        this.role = Mockito.mock( EditableRoleAccount.class );
    }

    @Test
    public void testSetMembers()
        throws Exception
    {
        testSetMembers( this.group );
        testSetMembers( this.role );
        testNoOperation( this.user, MembersEditor.Operation.SET );
    }

    @Test
    public void testAddMembers()
        throws Exception
    {
        testAddMembers( this.group );
        testAddMembers( this.role );
        testNoOperation( this.user, MembersEditor.Operation.ADD );
    }

    @Test
    public void testRemoveMembers()
        throws Exception
    {
        testRemoveMembers( this.group );
        testRemoveMembers( this.role );
        testNoOperation( this.user, MembersEditor.Operation.REMOVE );
    }

    private void testNoOperation( final EditableUserAccount account, final MembersEditor.Operation operation )
        throws Exception
    {
        final AccountKeySet keys = AccountKeySet.from( "user:other:dummy" );
        final MembersEditor editor = new MembersEditor( operation, keys );
        editor.edit( account );
    }

    private void testSetMembers( final EditableNonUserAccount account )
        throws Exception
    {
        final AccountKeySet keys = AccountKeySet.from( "user:other:dummy" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.SET, keys );

        editor.edit( account );

        Mockito.verify( account, Mockito.times( 1 ) ).setMembers( keys );
    }

    private void testAddMembers( final EditableNonUserAccount account )
        throws Exception
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.ADD, set2 );

        Mockito.when( account.getMembers() ).thenReturn( set1 );
        editor.edit( account );

        final AccountKeySet set3 = set1.add( set2 );
        Mockito.verify( account, Mockito.times( 1 ) ).setMembers( Mockito.eq( set3 ) );
    }

    private void testRemoveMembers( final EditableNonUserAccount account )
        throws Exception
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "role:other:admin" );
        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.REMOVE, set2 );

        Mockito.when( account.getMembers() ).thenReturn( set1 );
        editor.edit( account );

        final AccountKeySet set3 = set1.remove( set2 );
        Mockito.verify( account, Mockito.times( 1 ) ).setMembers( Mockito.eq( set3 ) );
    }
}
