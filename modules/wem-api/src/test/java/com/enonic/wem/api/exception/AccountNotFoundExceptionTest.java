package com.enonic.wem.api.exception;

import org.junit.Test;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountNotFoundException;

import static org.junit.Assert.*;

public class AccountNotFoundExceptionTest
{
    @Test
    public void testSimple()
    {
        final AccountNotFoundException ex = new AccountNotFoundException( AccountKey.from( "user:other:dummy" ) );
        assertEquals( "Account [user:other:dummy] was not found", ex.getMessage() );
    }
}
