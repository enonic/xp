package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.html.HtmlDocument;

@PublicApi
public interface HtmlPostProcessor
{
    void process( HtmlDocument document );
}
