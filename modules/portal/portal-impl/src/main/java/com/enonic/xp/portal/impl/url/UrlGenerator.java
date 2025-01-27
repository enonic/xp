package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.url.UrlParams;

public interface UrlGenerator<T extends UrlParams>
{
    String generateUrl( T params );
}
