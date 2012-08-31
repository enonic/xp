package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.Account;

final class CompositeEditor
    implements AccountEditor
{
    protected final AccountEditor[] editors;

    public CompositeEditor( final AccountEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public void edit( final Account account )
        throws Exception
    {
        for ( final AccountEditor editor : this.editors )
        {
            editor.edit( account );
        }
    }
}
