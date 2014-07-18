package com.enonic.wem.admin.rest.resource.content.json;

public class GetContentVersionsJson
{
    private Integer from;

    private Integer size;

    private final String contentId;

    public GetContentVersionsJson( final Integer from, final Integer size, final String contentId )
    {
        this.from = from;
        this.size = size;
        this.contentId = contentId;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Integer getFrom()
    {
        return from;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Integer getSize()
    {
        return size;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentId()
    {
        return contentId;
    }
}
