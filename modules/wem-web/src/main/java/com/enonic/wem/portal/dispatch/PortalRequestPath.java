package com.enonic.wem.portal.dispatch;

import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.portal.AbstractRequestPath;

public class PortalRequestPath
    extends AbstractRequestPath
{
    private static final String SPACE_PREFIX_DIVIDER = ":";

    private SpaceName spaceName;

    public PortalRequestPath( final SpaceName spaceName )
    {
        this.spaceName = spaceName;
    }

    public String getPathAsString()
    {
        return spaceName + SPACE_PREFIX_DIVIDER + getRelativePathAsString();
    }

    public SpaceName getSpaceName()
    {
        return spaceName;
    }
}
