package com.enonic.wem.api.account;


public final class UserKey
    extends AccountKey
{
    protected UserKey( final String userStore, final String localName )
    {
        super( AccountType.USER, userStore, localName );
    }
}
