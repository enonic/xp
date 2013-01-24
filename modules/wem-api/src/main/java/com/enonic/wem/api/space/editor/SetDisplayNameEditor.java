package com.enonic.wem.api.space.editor;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.space.Space;

import static com.enonic.wem.api.space.Space.newSpace;

final class SetDisplayNameEditor
    implements SpaceEditor
{
    private final String displayName;

    SetDisplayNameEditor( final String displayName )
    {
        Preconditions.checkNotNull( displayName, "displayName cannot be null" );
        this.displayName = displayName;
    }

    @Override
    public Space edit( final Space space )
        throws Exception
    {
        if ( space.equals( space.getDisplayName() ) )
        {
            return null;
        }
        return newSpace( space ).
            displayName( this.displayName ).
            build();
    }
}
