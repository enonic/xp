package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.GroupAccount;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;

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
        editor.edit( account );
    }

    private void testSetMembers( final NonUserAccount account )
        throws Exception
    {
        account.setMembers( AccountKeySet.empty() );

        final AccountKeySet keys = AccountKeySet.from( "user:other:dummy" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.SET, keys );

        editor.edit( account );

        assertEquals( keys, account.getMembers() );
    }

    private void testAddMembers( final NonUserAccount account )
        throws Exception
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy" );
        account.setMembers( set1 );

        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.ADD, set2 );

        editor.edit( account );

        final AccountKeySet set3 = set1.add( set2 );
        assertEquals( set3, account.getMembers() );
    }

    private void testRemoveMembers( final NonUserAccount account )
        throws Exception
    {
        final AccountKeySet set1 = AccountKeySet.from( "user:other:dummy", "role:other:admin" );
        account.setMembers( set1 );

        final AccountKeySet set2 = AccountKeySet.from( "role:other:admin" );
        final MembersEditor editor = new MembersEditor( MembersEditor.Operation.REMOVE, set2 );

        editor.edit( account );

        final AccountKeySet set3 = set1.remove( set2 );
        assertEquals( set3, account.getMembers() );
    }
}
