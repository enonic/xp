package com.enonic.xp.admin.impl.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.page.region.ComponentJson;
import com.enonic.xp.admin.impl.json.content.page.region.PageRegionsJson;
import com.enonic.xp.admin.impl.json.content.page.region.RegionJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyArrayJson;
import com.enonic.xp.data.PropertyTreeJson;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.UpdatePageParams;

public class UpdatePageJson
{
    private final UpdatePageParams updatePage;

    @JsonCreator
    public UpdatePageJson( @JsonProperty("contentId") final String contentId, @JsonProperty("controller") final String pageDescriptorKey,
                           @JsonProperty("template") final String pageTemplateKey,
                           @JsonProperty("config") final List<PropertyArrayJson> config,
                           @JsonProperty("regions") final List<RegionJson> regions, @JsonProperty("customized") final boolean customized,
                           @JsonProperty("fragment") final ComponentJson fragment )
    {
        this.updatePage = new UpdatePageParams().
            content( ContentId.from( contentId ) ).
            editor( toBeEdited -> {
                toBeEdited.controller = pageDescriptorKey != null ? DescriptorKey.from( pageDescriptorKey ) : null;
                toBeEdited.template = pageTemplateKey != null ? PageTemplateKey.from( pageTemplateKey ) : null;
                toBeEdited.regions = regions != null ? new PageRegionsJson( regions ).getPageRegions() : null;
                toBeEdited.fragment = fragment != null ? fragment.getComponent() : null;
                toBeEdited.config = config != null ? PropertyTreeJson.fromJson( config ) : null;
                toBeEdited.customized = customized;
            } );
    }

    @JsonIgnore
    public UpdatePageParams getUpdatePage()
    {
        return updatePage;
    }
}
