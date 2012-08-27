package com.enonic.wem.api.account;

public abstract class NonUserAccount
    extends Account
{
    private AccountKeySet members;

    public NonUserAccount( final AccountKey key )
    {
        super( key );
    }

    public final AccountKeySet getMembers()
    {
        return this.members;
    }

    public final void setMembers( final AccountKeySet members )
    {
        this.members = members;
        setDirtyFlag();
    }
}
