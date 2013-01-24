package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.space.SpaceName;

public final class SpaceNotFoundException
    extends BaseException
{
    public SpaceNotFoundException( final SpaceName spaceName )
    {
        super( "Space [{0}] was not found", spaceName );
    }
}
