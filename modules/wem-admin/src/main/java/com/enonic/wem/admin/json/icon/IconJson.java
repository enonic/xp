package com.enonic.wem.admin.json.icon;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.icon.Icon;

public class IconJson
{
    private final Icon icon;

    @JsonIgnore
    public IconJson( final Icon icon )
    {
        this.icon = icon;
    }

    @JsonCreator
    public IconJson( @JsonProperty("blobKey") final String blobKey, @JsonProperty("mimeType") final String mimeType )
    {
        this.icon = Icon.from( new BlobKey( blobKey ), mimeType );
    }

    public String getBlobKey()
    {
        return this.icon.getBlobKey().toString();
    }

    public String getMimeType()
    {
        return this.icon.getMimeType();
    }

    @JsonIgnore
    public Icon getIcon()
    {
        return icon;
    }
}
