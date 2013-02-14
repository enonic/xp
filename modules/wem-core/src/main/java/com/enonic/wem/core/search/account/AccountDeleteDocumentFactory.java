package com.enonic.wem.core.search.account;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.core.search.DeleteDocument;
import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.IndexType;

public class AccountDeleteDocumentFactory
{

    private AccountDeleteDocumentFactory()
    {
    }

    public static DeleteDocument create( final AccountKey accountKey )
    {
        return new DeleteDocument( IndexConstants.WEM_INDEX, IndexType.ACCOUNT, accountKey.toString() );
    }

}
