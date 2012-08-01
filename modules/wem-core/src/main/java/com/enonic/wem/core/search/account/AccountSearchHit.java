package com.enonic.wem.core.search.account;

public final class AccountSearchHit
{
    private final AccountKey key;

    private final AccountType accountType;

    private final float score;

    public AccountSearchHit( AccountKey key, AccountType accountType, float score )
    {
        this.key = key;
        this.accountType = accountType;
        this.score = score;
    }

    public AccountKey getKey()
    {
        return this.key;
    }

    public float getScore()
    {
        return this.score;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final AccountSearchHit that = (AccountSearchHit) o;

        if ( Float.compare( that.score, score ) != 0 )
        {
            return false;
        }
        if ( accountType != that.accountType )
        {
            return false;
        }
        if ( key != null ? !key.equals( that.key ) : that.key != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + ( accountType != null ? accountType.hashCode() : 0 );
        result = 31 * result + ( score != +0.0f ? Float.floatToIntBits( score ) : 0 );
        return result;
    }

    public AccountType getAccountType()
    {
        return accountType;
    }
}
