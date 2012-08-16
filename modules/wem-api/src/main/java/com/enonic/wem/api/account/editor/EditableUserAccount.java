package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.UserAccount;

public interface EditableUserAccount
    extends EditableAccount, UserAccount
{
    public void setEmail( String value );

    public void setPhoto( byte[] value );
}
