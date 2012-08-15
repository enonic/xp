package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.RoleAccount;

import static org.junit.Assert.*;

public class CompositeEditorTest
{
    private Account account;

    private AccountEditor editorEdit;

    private AccountEditor editorNoEdit;

    @Before
    public void setUp()
        throws Exception
    {
        this.account = RoleAccount.create( "other:dummy" );

        this.editorEdit = Mockito.mock( AccountEditor.class );
        Mockito.when( this.editorEdit.edit( this.account ) ).thenReturn( true );

        this.editorNoEdit = Mockito.mock( AccountEditor.class );
        Mockito.when( this.editorNoEdit.edit( this.account ) ).thenReturn( false );
    }

    @Test
    public void testNone()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor();
        assertFalse( editor.edit( this.account ) );
    }

    @Test
    public void testSingle1()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editorEdit );
        assertTrue( editor.edit( this.account ) );
        Mockito.verify( this.editorEdit, Mockito.times( 1 ) ).edit( this.account );
    }

    @Test
    public void testSingle2()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editorNoEdit );
        assertFalse( editor.edit( this.account ) );
        Mockito.verify( this.editorNoEdit, Mockito.times( 1 ) ).edit( this.account );
    }

    @Test
    public void testMultiple1()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editorEdit, this.editorEdit );
        assertTrue( editor.edit( this.account ) );
        Mockito.verify( this.editorEdit, Mockito.times( 2 ) ).edit( this.account );
    }

    @Test
    public void testMultiple2()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editorNoEdit, this.editorNoEdit );
        assertFalse( editor.edit( this.account ) );
        Mockito.verify( this.editorNoEdit, Mockito.times( 2 ) ).edit( this.account );
    }

    @Test
    public void testMultiple3()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editorNoEdit, this.editorEdit );
        assertTrue( editor.edit( this.account ) );
        Mockito.verify( this.editorEdit, Mockito.times( 1 ) ).edit( this.account );
        Mockito.verify( this.editorNoEdit, Mockito.times( 1 ) ).edit( this.account );
    }
}
