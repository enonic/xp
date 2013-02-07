package com.enonic.wem.core.search;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.search.account.AccountIndexDocumentFactory;
import com.enonic.wem.core.search.content.ContentIndexDocumentsFactory;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

@Component
public class IndexDocumentFactory
{
    private ContentIndexDocumentsFactory contentIndexDocumentsFactory;

    private AccountIndexDocumentFactory accountIndexDocumentFactory;

    public Collection<IndexDocument> create( Object indexableData )
    {

        if ( indexableData instanceof Content )
        {
            return contentIndexDocumentsFactory.create( (Content) indexableData );
        }
        else if ( indexableData instanceof Account )
        {
            return accountIndexDocumentFactory.create( (Account) indexableData );
        }

        return null;
    }


    @Autowired
    public void setContentIndexDocumentsFactory( final ContentIndexDocumentsFactory contentIndexDocumentsFactory )
    {
        this.contentIndexDocumentsFactory = contentIndexDocumentsFactory;
    }

    @Autowired
    public void setAccountIndexDocumentFactory( final AccountIndexDocumentFactory accountIndexDocumentFactory )
    {
        this.accountIndexDocumentFactory = accountIndexDocumentFactory;
    }
}
