package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.ExtraDataJson;
import com.enonic.xp.admin.impl.json.content.attachment.AttachmentJson;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.web.multipart.MultipartItem;

public final class CreateMediaFromUrlJson
{
    private final String parent;
    private final String name;
    private final String url;

    @JsonCreator
    CreateMediaFromUrlJson( @JsonProperty("parent") final String parent, @JsonProperty("name") final String name, @JsonProperty("url") final String url )
    {
       this.parent = parent;
       this.name = name;
       this.url = url;
    }

    public String getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public String getUrl()
    {
        return url;
    }
}
