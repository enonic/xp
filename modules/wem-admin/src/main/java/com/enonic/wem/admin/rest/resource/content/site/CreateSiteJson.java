package com.enonic.wem.admin.rest.resource.content.site;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.content.site.ModuleConfigJson;
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class CreateSiteJson
{
    private final CreateSite createSite;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    CreateSiteJson( @JsonProperty("contentId") String contentId,
                      @JsonProperty("siteTemplateKey") String siteTemplateKey,
                      @JsonProperty("moduleConfigs") List<ModuleConfigJson> moduleConfigs )
    {
        this.createSite = new CreateSite().
            content( ContentId.from( contentId ) ).
            template( SiteTemplateKey.from( siteTemplateKey ) ).
            moduleConfigs( ModuleConfigJson.toModuleConfigs( moduleConfigs ) );
    }

    @JsonIgnore
    CreateSite getCreateSite()
    {
        return this.createSite;
    }

}
