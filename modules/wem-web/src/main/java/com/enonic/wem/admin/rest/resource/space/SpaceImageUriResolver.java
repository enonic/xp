package com.enonic.wem.admin.rest.resource.space;

import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class SpaceImageUriResolver
{

    public static String resolve( final SpaceName spaceName )
    {
        final String spaceValue = spaceName.toString();
        return ServletRequestUrlHelper.createUrl( "/admin/rest/space/image/" + spaceValue );
    }

}
