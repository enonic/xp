package com.enonic.wem.api.account;

public abstract class NonUserAccount
    extends Account
{
    private AccountKeys members;

    public NonUserAccount( final AccountKey key )
    {
        super( key );
    }

    public final AccountKeys getMembers()
    {
        return this.members;
    }

    public final void setMembers( final AccountKeys members )
    {
        this.members = members;
        setDirtyFlag();
    }
}
