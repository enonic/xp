package com.enonic.wem.admin.rest.resource.schema.content.json;


public class ContentTypeCreateOrUpdateParams
{

    private String contentType;

    private String iconReference;

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getIconReference()
    {
        return iconReference;
    }

    public void setIconReference( final String iconReference )
    {
        this.iconReference = iconReference;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

}
