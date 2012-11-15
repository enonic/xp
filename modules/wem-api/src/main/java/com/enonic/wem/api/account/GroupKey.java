package com.enonic.wem.api.account;


public final class GroupKey
    extends AccountKey
{
    protected GroupKey( final String userStore, final String localName )
    {
        super( AccountType.GROUP, userStore, localName );
    }
}
