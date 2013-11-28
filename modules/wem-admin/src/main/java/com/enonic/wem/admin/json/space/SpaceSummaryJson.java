package com.enonic.wem.admin.json.space;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.rest.resource.space.SpaceImageUriResolver;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.support.serializer.JsonSerializerUtil;

public class SpaceSummaryJson
    implements ItemJson
{
    private final Space space;

    private final String iconUrl;

    public SpaceSummaryJson( final Space space )
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

    public boolean getEditable()
    {
        return !space.getName().toString().equals( "default" );
    }

    public boolean getDeletable()
    {
        return !space.getName().toString().equals( "default" );
    }
}
