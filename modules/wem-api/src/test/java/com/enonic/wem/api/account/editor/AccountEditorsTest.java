package com.enonic.wem.api.account.editor;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.mutable.MutableAccount;

import static org.junit.Assert.*;

public class AccountEditorsTest
{
    @Test
    public void testComposite()
        throws Exception
    {
        final AccountEditor editor1 = Mockito.mock( AccountEditor.class );
        final AccountEditor editor2 = Mockito.mock( AccountEditor.class );

        final AccountEditor editor3 = AccountEditors.composite( editor1, editor2 );
        assertNotNull( editor3 );
        assertTrue( editor3 instanceof CompositeEditor);

        final MutableAccount account = Mockito.mock( MutableAccount.class );
        editor3.edit( account );

        Mockito.verify( editor1, Mockito.times( 1 ) ).edit( account );
        Mockito.verify( editor2, Mockito.times( 1 ) ).edit( account );
    }
}
