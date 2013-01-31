package com.enonic.wem.core.search.account;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.core.search.IndexData;

@Component
public class AccountIndexDataFactory
{
    public IndexData create( Account account )
    {
        AccountIndexData accountIndexData = new AccountIndexData( account );

        return accountIndexData;
    }

}
