package com.enonic.cms.web.rest.account;

import com.enonic.cms.web.rest.common.RestResponse;

public class PhotoRestResponse extends RestResponse
{
    private String src;

    private String photoRef;

    public String getSrc()
    {
        return src;
    }

    public void setSrc( String src )
    {
        this.src = src;
    }

    public String getPhotoRef()
    {
        return photoRef;
    }

    public void setPhotoRef( String photoRef )
    {
        this.photoRef = photoRef;
    }
}
