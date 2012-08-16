package com.enonic.wem.api.account.editor;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.UserAccount;

import static org.junit.Assert.*;

public class AccountEditorsTest
{
    @Test
    public void testSetMembers()
        throws Exception
    {
        final AccountKeySet set = AccountKeySet.empty();
        final AccountEditor editor = AccountEditors.setMembers( set );

        verifyMembersEditor( editor, set, MembersEditor.Operation.SET );
    }

    @Test
    public void testAddMembers()
        throws Exception
    {
        final AccountKeySet set = AccountKeySet.empty();
        final AccountEditor editor = AccountEditors.addMembers( set );

        verifyMembersEditor( editor, set, MembersEditor.Operation.ADD );
    }

    @Test
    public void testRemoveMembers()
        throws Exception
    {
        final AccountKeySet set = AccountKeySet.empty();
        final AccountEditor editor = AccountEditors.removeMembers( set );

        verifyMembersEditor( editor, set, MembersEditor.Operation.REMOVE );
    }

    private void verifyMembersEditor( final AccountEditor editor, final AccountKeySet set, final MembersEditor.Operation operation )
        throws Exception
    {
        assertTrue( editor instanceof MembersEditor );

        final MembersEditor membersEditor = (MembersEditor) editor;
        assertSame( set, membersEditor.keys );
        assertEquals( operation, membersEditor.operation );
    }

    @Test
    public void testComposite()
        throws Exception
    {
        final AccountEditor editor1 = Mockito.mock( AccountEditor.class );
        final AccountEditor editor2 = Mockito.mock( AccountEditor.class );

        final AccountEditor editor3 = AccountEditors.composite( editor1, editor2 );

        assertTrue( editor3 instanceof CompositeEditor );

        final CompositeEditor compositeEditor = (CompositeEditor) editor3;
        assertNotNull( compositeEditor.editors );
        assertEquals( 2, compositeEditor.editors.length );
        assertSame( editor1, compositeEditor.editors[0] );
        assertSame( editor2, compositeEditor.editors[1] );
    }

    @Test
    public void testSetAccount()
        throws Exception
    {
        final UserAccount account = Mockito.mock( UserAccount.class );
        final AccountEditor editor = AccountEditors.setAccount( account );

        assertNotNull( editor );
        assertTrue( editor instanceof SetAccountEditor);
        assertSame( account, ((SetAccountEditor)editor).source );
    }
}
