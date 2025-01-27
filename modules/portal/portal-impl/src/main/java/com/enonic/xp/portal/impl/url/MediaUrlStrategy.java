package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.url.ImageMediaUrlParams;

public interface MediaUrlStrategy
{
    String generateUrl(final ImageMediaUrlParams params);
}
