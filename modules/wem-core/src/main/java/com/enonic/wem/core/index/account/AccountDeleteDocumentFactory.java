package com.enonic.wem.core.index.account;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.IndexConstants;
import com.enonic.wem.core.index.IndexType;

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
