package com.enonic.wem.admin.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.region.PageRegionsJson;
import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.CreatePageParams;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.DataJson;
import com.enonic.wem.api.data.RootDataSetJson;

public class CreatePageJson
{
    private final CreatePageParams createPage;

    @JsonCreator
    public CreatePageJson( final @JsonProperty("contentId") String contentId, final @JsonProperty("template") String pageTemplateKey,
                           final @JsonProperty("config") List<DataJson> config, final @JsonProperty("regions") List<RegionJson> regions )
    {
        this.createPage = new CreatePageParams().
            content( ContentId.from( contentId ) ).
            pageTemplate( PageTemplateKey.from( pageTemplateKey ) ).
            config( config != null ? new RootDataSetJson( config ).getRootDataSet() : null ).
            regions( regions != null ? new PageRegionsJson( regions ).getPageRegions() : null );
    }

    @JsonIgnore
    public CreatePageParams getCreatePage()
    {
        return createPage;
    }
}
