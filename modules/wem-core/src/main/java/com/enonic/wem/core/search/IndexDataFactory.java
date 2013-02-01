package com.enonic.wem.core.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.search.account.AccountIndexDataFactory;


@Component
public class IndexDataFactory
{
    private AccountIndexDataFactory accountIndexDataFactory;

    public IndexData createIndexDataForObject( Object indexableData )
    {

        if ( indexableData instanceof Account )
        {
            return accountIndexDataFactory.create( (Account) indexableData );
        }
        else if ( indexableData instanceof Content )
        {

        }

        return null;
    }

    @Autowired
    public void setAccountIndexDataFactory( final AccountIndexDataFactory accountIndexDataFactory )
    {
        this.accountIndexDataFactory = accountIndexDataFactory;
    }
}
