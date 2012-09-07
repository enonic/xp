package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.mockito.Mockito;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;

public class CompositeEditorTest
{
    private UserAccount account;

    private AccountEditor editor1;

    private AccountEditor editor2;

    @Before
    public void setUp()
        throws Exception
    {
        this.account = UserAccount.create( "other:dummy" );
        this.editor1 = Mockito.mock( AccountEditor.class );
        Mockito.when( this.editor1.edit( Mockito.any( Account.class ) ) ).thenReturn( true );
        this.editor2 = Mockito.mock( AccountEditor.class );
    }

    @Test
    public void testNone()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor();
        assertFalse( editor.edit( this.account ) );
    }

    @Test
    public void testSingle()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editor1 );
        assertTrue( editor.edit( this.account ) );
        Mockito.verify( this.editor1, Mockito.times( 1 ) ).edit( this.account );
    }

    @Test
    public void testMultiple()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editor1, this.editor2 );
        assertTrue( editor.edit( this.account ) );
        Mockito.verify( this.editor1, Mockito.times( 1 ) ).edit( this.account );
        Mockito.verify( this.editor2, Mockito.times( 1 ) ).edit( this.account );
    }
}
