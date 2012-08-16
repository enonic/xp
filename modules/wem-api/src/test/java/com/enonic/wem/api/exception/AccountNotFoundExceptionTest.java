package com.enonic.wem.api.exception;

import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.api.account.AccountKey;

public class AccountNotFoundExceptionTest
{
    @Test
    public void testSimple()
    {
        final AccountNotFoundException ex = new AccountNotFoundException( AccountKey.from( "user:other:dummy" ) );
        assertEquals( "Account [user:other:dummy] was not found", ex.getMessage() );
    }
}
