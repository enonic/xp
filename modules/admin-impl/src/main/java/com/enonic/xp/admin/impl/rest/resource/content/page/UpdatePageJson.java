package com.enonic.xp.admin.impl.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.admin.impl.json.content.page.region.PageRegionsJson;
import com.enonic.xp.admin.impl.json.content.page.region.RegionJson;
import com.enonic.xp.core.content.ContentId;
import com.enonic.xp.core.content.page.DescriptorKey;
import com.enonic.xp.core.content.page.PageTemplateKey;
import com.enonic.xp.core.content.page.UpdatePageParams;
import com.enonic.xp.core.data.PropertyArrayJson;
import com.enonic.xp.core.data.PropertyTreeJson;

public class UpdatePageJson
{
    private final UpdatePageParams updatePage;

    @JsonCreator
    public UpdatePageJson( @JsonProperty("contentId") final String contentId, @JsonProperty("controller") final String pageDescriptorKey,
                           @JsonProperty("template") final String pageTemplateKey,
                           @JsonProperty("config") final List<PropertyArrayJson> config,
                           final @JsonProperty("regions") List<RegionJson> regions )
    {
        this.updatePage = new UpdatePageParams().
            content( ContentId.from( contentId ) ).
            editor( toBeEdited -> {
                toBeEdited.controller = pageDescriptorKey != null ? DescriptorKey.from( pageDescriptorKey ) : null;
                toBeEdited.template = pageTemplateKey != null ? PageTemplateKey.from( pageTemplateKey ) : null;
                toBeEdited.regions = regions != null ? new PageRegionsJson( regions ).getPageRegions() : null;
                toBeEdited.config = config != null ? PropertyTreeJson.fromJson( config ) : null;
            } );
    }

    @JsonIgnore
    public UpdatePageParams getUpdatePage()
    {
        return updatePage;
    }
}
