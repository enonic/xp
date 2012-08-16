package com.enonic.wem.api.account.editor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CompositeEditorTest
{
    private EditableUserAccount account;

    private AccountEditor editor1;

    private AccountEditor editor2;

    @Before
    public void setUp()
        throws Exception
    {
        this.account = Mockito.mock( EditableUserAccount.class );
        this.editor1 = Mockito.mock( AccountEditor.class );
        this.editor2 = Mockito.mock( AccountEditor.class );
    }

    @Test
    public void testNone()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor();
        editor.edit( this.account );
    }

    @Test
    public void testSingle()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editor1 );
        editor.edit( this.account );
        Mockito.verify( this.editor1, Mockito.times( 1 ) ).edit( this.account );
    }

    @Test
    public void testMultiple()
        throws Exception
    {
        final CompositeEditor editor = new CompositeEditor( this.editor1, this.editor2 );
        editor.edit( this.account );
        Mockito.verify( this.editor1, Mockito.times( 1 ) ).edit( this.account );
        Mockito.verify( this.editor2, Mockito.times( 1 ) ).edit( this.account );
    }
}
