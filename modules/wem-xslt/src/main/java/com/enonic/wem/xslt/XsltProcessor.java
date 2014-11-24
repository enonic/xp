package com.enonic.wem.xslt;

import javax.xml.transform.Source;

import com.enonic.wem.api.resource.ResourceKey;

public interface XsltProcessor
{
    public XsltProcessor view( ResourceKey view );

    public XsltProcessor inputSource( Source inputSource );

    public String process();
}
