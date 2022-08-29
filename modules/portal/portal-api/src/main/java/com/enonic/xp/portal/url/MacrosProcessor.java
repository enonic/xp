package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MacrosProcessor
{
    String process( String html );
}
