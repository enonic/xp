package com.enonic.xp.admin.impl.rest.resource.macro.json;

import java.util.List;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.HtmlTag;

public final class PageContributionsJson
{
    private final List<String> headBegin;

    private final List<String> headEnd;

    private final List<String> bodyBegin;

    private final List<String> bodyEnd;

    public PageContributionsJson( final PortalResponse response )
    {
        this.headBegin = response.getContributions( HtmlTag.HEAD_BEGIN );
        this.headEnd = response.getContributions( HtmlTag.HEAD_END );
        this.bodyBegin = response.getContributions( HtmlTag.BODY_BEGIN );
        this.bodyEnd = response.getContributions( HtmlTag.BODY_END );
    }

    public List<String> getHeadBegin()
    {
        return headBegin;
    }

    public List<String> getHeadEnd()
    {
        return headEnd;
    }

    public List<String> getBodyBegin()
    {
        return bodyBegin;
    }

    public List<String> getBodyEnd()
    {
        return bodyEnd;
    }
}
