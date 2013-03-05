package com.enonic.wem.api.command.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.editor.AccountEditor;

import static org.junit.Assert.*;

public class UpdateAccountsTest
{
    @Test
    public void testValid()
    {
        final UpdateAccounts command = new UpdateAccounts();

        assertNull( command.getKey() );
        assertNull( command.getEditor() );

        final AccountEditor editor = Mockito.mock( AccountEditor.class );
        final AccountKey key = AccountKey.from( "user:other:dummy" );

        command.key( key );
        command.editor( editor );
        assertSame( key, command.getKey() );
        assertSame( editor, command.getEditor() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullKey()
    {
        final UpdateAccounts command = new UpdateAccounts();
        command.key( null );

        final AccountEditor editor = Mockito.mock( AccountEditor.class );
        command.editor( editor );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullEditor()
    {
        final UpdateAccounts command = new UpdateAccounts();
        command.editor( null );

        final AccountKey key = AccountKey.from( "user:other:dummy" );
        command.key( key );

        command.validate();
    }
}
