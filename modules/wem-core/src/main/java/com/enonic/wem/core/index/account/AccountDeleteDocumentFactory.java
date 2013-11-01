package com.enonic.wem.core.index.account;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;

public class AccountDeleteDocumentFactory
{

    private AccountDeleteDocumentFactory()
    {
    }

    public static DeleteDocument create( final AccountKey accountKey )
    {
        return new DeleteDocument( Index.WEM, IndexType.ACCOUNT, accountKey.toString() );
    }

}
