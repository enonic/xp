package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface UrlStrategyFacade
{
    ImageUrlGeneratorParams offlineImageUrlParams( ImageUrlParams params );

    ImageUrlGeneratorParams requestImageUrlParams( ImageUrlParams params );

    AttachmentUrlGeneratorParams offlineAttachmentUrlParams( AttachmentUrlParams params );

    AttachmentUrlGeneratorParams requestAttachmentUrlParams( AttachmentUrlParams params );
}
