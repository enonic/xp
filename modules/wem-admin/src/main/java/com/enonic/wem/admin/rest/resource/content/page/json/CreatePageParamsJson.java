package com.enonic.wem.admin.rest.resource.content.page.json;


import com.fasterxml.jackson.databind.node.ArrayNode;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateId;

public class CreatePageParamsJson
{
    private ContentId contentId;

    private PageTemplateId pageTemplateId;

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

    public void setPageTemplateId( final String value )
    {
        this.pageTemplateId = new PageTemplateId( value );
    }

    public PageTemplateId getPageTemplateId()
    {
        return pageTemplateId;
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
