package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.PropertyTree;

public final class ResultMappers
{
    public static Object mapper( final Content value )
    {
        return value != null ? new ContentMapper( value ) : null;
    }

    public static Object mapper( final PropertyTree value )
    {
        return value != null ? new PropertyTreeMapper( value ) : null;
    }

    public static Object mapper( final Page value )
    {
        return value != null ? new PageMapper( value ) : null;
    }

    public static Object mapper( final Region value )
    {
        return value != null ? new RegionMapper( value ) : null;
    }

    public static Object mapper( final PageComponent value )
    {
        return value != null ? new PageComponentMapper( value ) : null;
    }
}
