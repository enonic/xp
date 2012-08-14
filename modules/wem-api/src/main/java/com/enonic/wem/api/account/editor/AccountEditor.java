package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.mutable.MutableAccount;

public interface AccountEditor
{
    public boolean edit( MutableAccount account )
        throws Exception;
}
