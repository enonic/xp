package com.enonic.wem.api.account;

public interface NonUserAccount
    extends Account
{
    public AccountKeySet getMembers();
}
