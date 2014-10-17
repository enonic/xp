package com.enonic.wem.api.identity;

public final class Anonymous
    extends Identity
{
    private static final Anonymous INSTANCE = new Anonymous();

    private Anonymous()
    {
        super( new Identity.Builder().
            identityKey( IdentityKey.ofAnonymous() ).
            displayName( "anonymous" ) );
    }

    public static Anonymous get()
    {
        return INSTANCE;
    }
}
