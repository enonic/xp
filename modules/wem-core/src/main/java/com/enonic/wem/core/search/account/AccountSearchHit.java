package com.enonic.wem.core.search.account;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountType;

public final class AccountSearchHit
{
    private final AccountKey key;

    private final float score;

    public AccountSearchHit( AccountKey key, float score )
    {
        this.key = key;
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

    public AccountType getAccountType()
    {
        return key.getType();
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
        result = 31 * result + ( score != +0.0f ? Float.floatToIntBits( score ) : 0 );
        return result;
    }
}
