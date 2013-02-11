package com.enonic.wem.core.search;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.search.account.AccountIndexDocumentFactory;
import com.enonic.wem.core.search.content.ContentIndexDocumentsFactory;
import com.enonic.wem.core.search.indexdocument.IndexDocument;

@Component
public class IndexDocumentFactory
{
    private IndexDocumentFactory()
    {
    }

    public static Collection<IndexDocument> create( Object indexableData )
    {
        if ( indexableData instanceof Content )
        {
            return ContentIndexDocumentsFactory.create( (Content) indexableData );
        }
        else if ( indexableData instanceof Account )
        {
            return AccountIndexDocumentFactory.create( (Account) indexableData );
        }

        return null;
    }
}
