package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;

public interface EditableAccount
    extends Account
{
    public void setDisplayName( String value );
}
