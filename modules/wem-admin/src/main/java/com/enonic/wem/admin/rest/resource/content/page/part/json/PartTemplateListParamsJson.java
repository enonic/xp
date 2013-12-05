package com.enonic.wem.admin.rest.resource.content.page.part.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class PartTemplateListParamsJson
{

    private SiteTemplateKey key;

    @JsonCreator
    public PartTemplateListParamsJson(@JsonProperty("siteTemplateKey") String key) {
        this.key = SiteTemplateKey.from( key );
    }

    public SiteTemplateKey getKey()
    {
        return key;
    }
}
