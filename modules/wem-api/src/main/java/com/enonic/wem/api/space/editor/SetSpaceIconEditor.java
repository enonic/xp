package com.enonic.wem.api.space.editor;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.space.Space;

final class SetSpaceIconEditor
    implements SpaceEditor
{
    private final Icon icon;

    SetSpaceIconEditor( final Icon icon )
    {
        this.icon = icon;
    }

    @Override
    public Space edit( final Space space )
        throws Exception
    {
        final Icon iconToSet = ( this.icon == null ) ? null : Icon.copyOf( this.icon );
        final Space updated = Space.newSpace( space ).
            icon( iconToSet ).
            build();
        return updated;
    }
}
