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
    public boolean edit( final Account account )
        throws Exception
    {
        boolean flag = false;
        for ( final AccountEditor editor : this.editors )
        {
            final boolean result = editor.edit( account );
            flag = flag || result;
        }

        return flag;
    }
}
