package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;

public interface AccountEditor
{
    public boolean edit( Account account )
        throws Exception;
}
