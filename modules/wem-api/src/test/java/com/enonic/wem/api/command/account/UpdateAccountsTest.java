package com.enonic.wem.api.command.account;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.editor.AccountEditor;
import com.enonic.wem.api.account.selector.AccountSelector;
import com.enonic.wem.api.account.selector.AccountSelectors;

import static org.junit.Assert.*;

public class UpdateAccountsTest
{
    @Test
    public void testValid()
    {
        final UpdateAccounts command = new UpdateAccounts();

        assertNull( command.getSelector() );
        assertNull( command.getEditor() );

        final AccountEditor editor = Mockito.mock( AccountEditor.class );
        final AccountSelector selector = AccountSelectors.keys( "user:other:dummy" );

        command.selector( selector );
        command.editor( editor );
        assertSame( selector, command.getSelector() );
        assertSame( editor, command.getEditor() );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullSelector()
    {
        final UpdateAccounts command = new UpdateAccounts();
        command.selector( null );

        final AccountEditor editor = Mockito.mock( AccountEditor.class );
        command.editor( editor );

        command.validate();
    }

    @Test(expected = NullPointerException.class)
    public void testNotValid_nullEditor()
    {
        final UpdateAccounts command = new UpdateAccounts();
        command.editor( null );

        final AccountSelector selector = AccountSelectors.keys( "user:other:dummy" );
        command.selector( selector );

        command.validate();
    }
}
