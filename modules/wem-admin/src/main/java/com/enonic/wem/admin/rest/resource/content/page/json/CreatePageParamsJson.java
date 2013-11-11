package com.enonic.wem.admin.rest.resource.content.page.json;


import com.fasterxml.jackson.databind.node.ArrayNode;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateName;

public class CreatePageParamsJson
{
    private ContentId contentId;

    private PageTemplateName pageTemplateName;

    private ArrayNode config;

    private ArrayNode liveEdit;

    private ArrayNode page;

    public void setContentId( final String value )
    {
        this.contentId = ContentId.from( value );
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public void setPageTemplateName( final String value )
    {
        this.pageTemplateName = new PageTemplateName( value );
    }

    public PageTemplateName getPageTemplateName()
    {
        return pageTemplateName;
    }

    public void setConfig( final ArrayNode value )
    {
        this.config = value;
    }

    public ArrayNode getConfig()
    {
        return config;
    }

    public void setLiveEdit( final ArrayNode value )
    {
        this.liveEdit = value;
    }

    public ArrayNode getLiveEdit()
    {
        return liveEdit;
    }

    public void setPage( final ArrayNode value )
    {
        this.page = value;
    }

    public ArrayNode getPage()
    {
        return page;
    }
}
