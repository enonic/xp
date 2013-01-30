package com.enonic.wem.api.space.editor;

import java.util.Arrays;

import com.enonic.wem.api.space.Space;

final class SetSpaceIconEditor
    implements SpaceEditor
{
    private final byte[] icon;

    SetSpaceIconEditor( final byte[] icon )
    {
        this.icon = icon;
    }

    @Override
    public Space edit( final Space space )
        throws Exception
    {
        final byte[] iconToSet = ( this.icon == null ) ? null : Arrays.copyOf( this.icon, this.icon.length );
        final Space updated = Space.newSpace( space ).
            icon( iconToSet ).
            build();
        return updated;
    }
}
