package com.enonic.wem.core.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.search.account.AccountIndexDataFactory;
import com.enonic.wem.core.search.content.ContentIndexDataFactory;


@Component
public class IndexDataFactory
{
    private AccountIndexDataFactory accountIndexDataFactory;

    private ContentIndexDataFactory contentIndexDataFactory;

    public IndexData createIndexDataForObject( final Object indexableData )
    {

        if ( indexableData instanceof Account )
        {
            return accountIndexDataFactory.create( (Account) indexableData );
        }
        else if ( indexableData instanceof Content )
        {
            return contentIndexDataFactory.create( (Content) indexableData );
        }

        return null;
    }

    @Autowired
    public void setAccountIndexDataFactory( final AccountIndexDataFactory accountIndexDataFactory )
    {
        this.accountIndexDataFactory = accountIndexDataFactory;
    }

    @Autowired
    public void setContentIndexDataFactory( final ContentIndexDataFactory contentIndexDataFactory )
    {
        this.contentIndexDataFactory = contentIndexDataFactory;
    }
}
