package com.enonic.wem.web.rest.account;

import java.util.ArrayList;
import java.util.List;

public class AccountsModel
{
    private int total;

    private final List<AccountModel> accounts;

    public AccountsModel()
    {
        this.accounts = new ArrayList<AccountModel>();
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public List<AccountModel> getAccounts()
    {
        return accounts;
    }

    public void addAccount( AccountModel account )
    {
        this.accounts.add( account );
    }
}
