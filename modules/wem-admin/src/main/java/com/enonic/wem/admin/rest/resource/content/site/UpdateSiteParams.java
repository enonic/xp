package com.enonic.wem.admin.rest.resource.content.site;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.site.ModuleConfigJson;
import com.enonic.wem.admin.json.site.ModuleConfigs;
import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteEditor;
import com.enonic.wem.api.content.site.SiteTemplateName;

public class UpdateSiteParams
{
    private final UpdateSite updateSite;

    @SuppressWarnings("UnusedDeclaration")
    @JsonCreator
    UpdateSiteParams( @JsonProperty("content") String content, @JsonProperty("siteTemplate") final String siteTemplate,
                      @JsonProperty("moduleConfigs") final List<ModuleConfigJson> moduleConfigs )
    {
        this.updateSite = new UpdateSite().
            content( ContentId.from( content ) ).
            editor( new SiteEditor()
            {
                @Override
                public Site.EditBuilder edit( final Site toBeEdited )
                {
                    return Site.editSite( toBeEdited ).
                        template( new SiteTemplateName( siteTemplate ) ).
                        moduleConfigs( ModuleConfigJson.toModuleConfigs( moduleConfigs ) );
                }
            } );
    }

    @JsonIgnore
    UpdateSite getUpdateSite()
    {
        return this.updateSite;
    }

}
