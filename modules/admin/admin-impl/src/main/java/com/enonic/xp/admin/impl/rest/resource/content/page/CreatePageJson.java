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
import com.enonic.xp.page.CreatePageParams;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.page.PageTemplateKey;

public class CreatePageJson
{
    private final CreatePageParams createPage;

    @JsonCreator
    public CreatePageJson( @JsonProperty("contentId") final String contentId, @JsonProperty("controller") String pageDescriptorKey,
                           @JsonProperty("template") final String pageTemplateKey,
                           @JsonProperty("config") final List<PropertyArrayJson> config,
                           @JsonProperty("regions") final List<RegionJson> regions, @JsonProperty("customized") final boolean customized,
                           @JsonProperty("fragment") final ComponentJson fragment )
    {
        this.createPage = new CreatePageParams().
            content( ContentId.from( contentId ) ).
            controller( pageDescriptorKey != null ? DescriptorKey.from( pageDescriptorKey ) : null ).
            pageTemplate( pageTemplateKey != null ? PageTemplateKey.from( pageTemplateKey ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            regions( regions != null ? new PageRegionsJson( regions ).getPageRegions() : null ).
            fragment( fragment != null ? fragment.getComponent() : null ).
            customized( customized );
    }

    @JsonIgnore
    public CreatePageParams getCreatePage()
    {
        return createPage;
    }
}
