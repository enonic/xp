package com.enonic.wem.api.account;


import com.google.common.base.Preconditions;

public final class UserKey
    extends AccountKey
{
    private UserKey( final String userStore, final String localName )
    {
        super( AccountType.USER, userStore, localName );
    }

    public static UserKey from( AccountKey accountKey )
    {
        Preconditions.checkArgument( accountKey.isUser() );
        return new UserKey( accountKey.getUserStore(), accountKey.getLocalName() );
    }

    public static UserKey from( String qName )
    {
        return from( AccountKey.user( qName ) );
    }
}
