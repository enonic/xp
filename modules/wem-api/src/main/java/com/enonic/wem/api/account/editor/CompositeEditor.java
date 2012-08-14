package com.enonic.wem.api.account.editor;

import com.enonic.wem.api.account.mutable.MutableAccount;

final class CompositeEditor
    implements AccountEditor
{
    private final AccountEditor[] editors;

    public CompositeEditor( final AccountEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public boolean edit( final MutableAccount account )
        throws Exception
    {
        boolean result = false;
        for ( final AccountEditor editor : this.editors )
        {
            final boolean flag = editor.edit( account );
            result = result || flag;
        }

        return result;
    }
}
