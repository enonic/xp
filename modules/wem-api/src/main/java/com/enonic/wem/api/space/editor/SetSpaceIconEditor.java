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
    {
        final Space updated = Space.newSpace( space ).
            icon( this.icon ).
            build();
        return updated;
    }
}
