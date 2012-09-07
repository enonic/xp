package com.enonic.wem.api.command.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.editor.AccountEditor;

import static org.junit.Assert.*;

public class UpdateAccountsTest
{
    @Test
    public void testValid()
    {
        final UpdateAccounts command = new UpdateAccounts();

        assertNull( command.getKeys() );
        assertNull( command.getEditor() );

        final AccountEditor editor = Mockito.mock( AccountEditor.class );
        final AccountKeys keys = AccountKeys.from( "user:other:dummy" );

        command.keys( keys );
        command.editor( editor );
        assertSame( keys, command.getKeys() );
        assertSame( editor, command.getEditor() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKeys()
    {
        final UpdateAccounts command = new UpdateAccounts();
        command.keys( null );

        final AccountEditor editor = Mockito.mock( AccountEditor.class );
        command.editor( editor );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullEditor()
    {
        final UpdateAccounts command = new UpdateAccounts();
        command.editor( null );

        final AccountKeys keys = AccountKeys.from( "user:other:dummy" );
        command.keys( keys );

        command.validate();
    }
}
