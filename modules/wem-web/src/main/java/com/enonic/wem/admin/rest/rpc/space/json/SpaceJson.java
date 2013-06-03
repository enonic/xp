package com.enonic.wem.admin.rest.rpc.space.json;

import org.codehaus.jackson.annotate.JsonPropertyOrder;

import com.enonic.wem.admin.rest.resource.space.SpaceImageUriResolver;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

@JsonPropertyOrder(alphabetic = true)
public class SpaceJson
{
    private final Space space;

    private final String iconUrl;

    public SpaceJson( final Space space )
    {
        this.space = space;
        this.iconUrl = SpaceImageUriResolver.resolve( space.getName() );
    }

    public String getName()
    {
        return space.getName().toString();
    }

    public String getDisplayName()
    {
        return space.getDisplayName();
    }

    public String getCreatedTime()
    {
        return JsonSerializerUtil.isoDateTimeFormatter.print( space.getCreatedTime() );
    }

    public String getModifiedTime()
    {
        return JsonSerializerUtil.isoDateTimeFormatter.print( space.getModifiedTime() );
    }

    public String getRootContentId()
    {
        return space.getRootContent().toString();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }
}
