package com.enonic.xp.admin.impl.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    public CreatePageJson( final @JsonProperty("contentId") String contentId, @JsonProperty("controller") final String pageDescriptorKey,
                           final @JsonProperty("template") String pageTemplateKey,
                           final @JsonProperty("config") List<PropertyArrayJson> config,
                           final @JsonProperty("regions") List<RegionJson> regions )
    {
        this.createPage = new CreatePageParams().
            content( ContentId.from( contentId ) ).
            controller( pageDescriptorKey != null ? DescriptorKey.from( pageDescriptorKey ) : null ).
            pageTemplate( pageTemplateKey != null ? PageTemplateKey.from( pageTemplateKey ) : null ).
            config( config != null ? PropertyTreeJson.fromJson( config ) : null ).
            regions( regions != null ? new PageRegionsJson( regions ).getPageRegions() : null );
    }

    @JsonIgnore
    public CreatePageParams getCreatePage()
    {
        return createPage;
    }
}
