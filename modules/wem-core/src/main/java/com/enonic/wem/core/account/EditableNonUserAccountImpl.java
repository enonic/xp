package com.enonic.wem.core.account;

import com.enonic.wem.api.account.AccountKeySet;
import com.enonic.wem.api.account.editor.EditableNonUserAccount;

abstract class EditableNonUserAccountImpl
    extends EditableAccountImpl
    implements EditableNonUserAccount
{
    private AccountKeySet members;

    @Override
    public final AccountKeySet getMembers()
    {
        return this.members;
    }

    @Override
    public final void setMembers( final AccountKeySet value )
    {
        setModified();
        this.members = value;
    }
}
