package com.enonic.wem.admin.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.page.region.PageRegionsJson;
import com.enonic.wem.admin.json.content.page.region.RegionJson;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageEditor;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.UpdatePageParams;
import com.enonic.wem.api.data.PropertyArrayJson;
import com.enonic.wem.api.data.PropertyTreeJson;

import static com.enonic.wem.api.content.page.Page.editPage;

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
            editor( new PageEditor()
            {
                @Override
                public Page.PageEditBuilder edit( final Page toBeEdited )
                {
                    return editPage( toBeEdited ).
                        controller( pageDescriptorKey != null ? PageDescriptorKey.from( pageDescriptorKey ) : null ).
                        template( pageTemplateKey != null ? PageTemplateKey.from( pageTemplateKey ) : null ).
                        regions( regions != null ? new PageRegionsJson( regions ).getPageRegions() : null ).
                        config( config != null ? PropertyTreeJson.fromJson( config ) : null );
                }
            } );
    }

    @JsonIgnore
    public UpdatePageParams getUpdatePage()
    {
        return updatePage;
    }
}
