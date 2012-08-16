package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.NonUserAccount;

public interface EditableNonUserAccount
    extends EditableAccount, NonUserAccount
{
    public void setMembers( AccountKeySet value );
}
