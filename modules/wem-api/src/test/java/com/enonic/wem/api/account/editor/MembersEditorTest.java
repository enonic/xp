package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

import static org.junit.Assert.*;

public class MembersEditorTest
{
    private UserAccount user;

    private GroupAccount group;

    private RoleAccount role;

    @Before
    public void setUp()
    {
        this.user = UserAccount.create( "other:dummy" );
        this.group = GroupAccount.create( "other:dummy" );
        this.role = RoleAccount.create( "other:dummy" );
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

    private void testNoOperation( final UserAccount account, final MembersEditor.Operation operation )
        throws Exception
    {
        final AccountKeySet keys = AccountKeySet.from( "user:other:dummy" );
        final MembersEditor editor = new MembersEditor( operation, keys );

        final boolean flag = editor.edit( account );
        assertFalse( flag );
    }

    private void testSetMembers( final NonUserAccount account )
        throws Exception
    {
        final AccountKeySet keys = AccountKeySet.from( "user:other:dummy" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.SET, keys );

        final boolean flag = editor.edit( account );
        assertTrue( flag );

        assertNotNull( account.getMembers() );
        assertTrue( account.getMembers().contains( AccountKey.from( "user:other:dummy" ) ) );
    }

    private void testAddMembers( final NonUserAccount account )
        throws Exception
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy" );
        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.ADD, set2 );

        account.members( set1 );

        final boolean flag = editor.edit( account );
        assertTrue( flag );

        assertNotNull( account.getMembers() );
        assertEquals( 2, account.getMembers().getSize() );
        assertTrue( account.getMembers().contains( AccountKey.from( "role:other:admin" ) ) );
    }

    private void testRemoveMembers( final NonUserAccount account )
        throws Exception
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "role:other:admin" );
        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.REMOVE, set2 );

        account.members( set1 );

        final boolean flag = editor.edit( account );
        assertTrue( flag );

        assertNotNull( account.getMembers() );
        assertEquals( 1, account.getMembers().getSize() );
        assertFalse( account.getMembers().contains( AccountKey.from( "role:other:admin" ) ) );
    }
}
