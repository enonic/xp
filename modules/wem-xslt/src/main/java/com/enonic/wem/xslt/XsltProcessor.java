package com.enonic.wem.xslt;

import java.util.Map;

import javax.xml.transform.Source;

import com.enonic.wem.api.resource.ResourceKey;

public interface XsltProcessor
{
    public XsltProcessor view( ResourceKey view );

    public XsltProcessor inputXml( String inputXml );

    public XsltProcessor inputSource( Source inputSource );

    public XsltProcessor parameters( Map<String, Object> parameters );

    public String process();
}
