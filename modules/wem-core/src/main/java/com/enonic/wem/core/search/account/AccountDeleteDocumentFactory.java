package com.enonic.wem.core.search.account;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.core.search.DeleteDocument;
import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.IndexType;

public class AccountDeleteDocumentFactory
{

    private AccountDeleteDocumentFactory()
    {
    }

    public static DeleteDocument create( final Account account )
    {
        return new DeleteDocument( IndexConstants.WEM_INDEX.value(), IndexType.ACCOUNT, account.getKey().toString() );
    }

}
