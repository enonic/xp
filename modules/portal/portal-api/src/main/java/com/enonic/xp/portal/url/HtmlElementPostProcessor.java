package com.enonic.xp.portal.url;

import java.util.Map;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.html.HtmlElement;

@PublicApi
@FunctionalInterface
public interface HtmlElementPostProcessor
{
    void process( HtmlElement element, Map<String, String> properties );
}
