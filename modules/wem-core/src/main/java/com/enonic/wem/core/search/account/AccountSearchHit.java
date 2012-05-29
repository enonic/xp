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

    public AccountType getAccountType()
    {
        return accountType;
    }
}
