package com.enonic.xp.admin.impl.rest.resource.content.page.fragment;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.page.region.ComponentJson;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.CreateFragmentParams;

public class CreateFragmentJson
{
    private final CreateFragmentParams createFragmentParams;

    @JsonCreator
    public CreateFragmentJson( @JsonProperty("contentPath") final String contentPath,
                               @JsonProperty("config") final List<PropertyArrayJson> configJson,
                               @JsonProperty("component") final ComponentJson componentJson )
    {
        final Component component = componentJson != null ? componentJson.getComponent() : null;
        final PropertyTree config = configJson != null ? PropertyTreeJson.fromJson( configJson ) : null;

        this.createFragmentParams = CreateFragmentParams.create().
            parent( ContentPath.from( contentPath ) ).
            component( component ).
            config( config ).
            build();
    }

    @JsonIgnore
    public CreateFragmentParams getCreateFragmentParams()
    {
        return createFragmentParams;
    }
}
