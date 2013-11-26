package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.content.site.SiteTemplateKey;

public class DeleteSiteTemplateParams
{

    private SiteTemplateKey key;

    @JsonCreator
    public DeleteSiteTemplateParams(@JsonProperty("key") String key) {

        this.key = SiteTemplateKey.from( key );
    }

    public SiteTemplateKey getKey()
    {
        return key;
    }
}
