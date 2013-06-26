package com.enonic.wem.admin.rest.rpc.space.json;


import com.enonic.wem.admin.rest.resource.space.SpaceImageUriResolver;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

public class SpaceRowJson
{
    private final Space space;

    public SpaceRowJson( final Space space )
    {
        this.space = space;
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
        return SpaceImageUriResolver.resolve( space.getName() );
    }

    public boolean getEditable()
    {
        return !space.isTemporary() && !space.getName().toString().equals( "default" );
    }

    public boolean getDeletable()
    {
        return !space.isTemporary() && !space.getName().toString().equals( "default" );
    }
}
