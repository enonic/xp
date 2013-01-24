package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.space.SpaceName;

public final class SpaceAlreadyExistException
    extends BaseException
{
    public SpaceAlreadyExistException( final SpaceName spaceName )
    {
        super( "Space with name [{0}] already exist", spaceName.toString() );
    }
}
