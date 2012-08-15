package com.enonic.wem.api.command.account;

import org.junit.Test;

public class CreateAccountTest
{
    @Test
    public void testValid()
    {
        final CreateAccount command = new CreateAccount();

        command.validate();
    }
}
