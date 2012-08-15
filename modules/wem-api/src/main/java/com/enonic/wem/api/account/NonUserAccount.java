package com.enonic.wem.api.account;

public abstract class NonUserAccount<T extends NonUserAccount>
    extends Account<T>
{
    private AccountKeySet members;

    protected NonUserAccount( final AccountKey key )
    {
        super( key );
        this.members = AccountKeySet.empty();
    }

    public final AccountKeySet getMembers()
    {
        return this.members;
    }

    public final T members( final AccountKeySet members )
    {
        if ( members == null )
        {
            this.members = AccountKeySet.empty();
        }
        else
        {
            this.members = members;
        }

        return getThis();
    }

    protected final void copyTo( final NonUserAccount target )
    {
        super.copyTo( target );
        target.members = this.members;
    }
}
