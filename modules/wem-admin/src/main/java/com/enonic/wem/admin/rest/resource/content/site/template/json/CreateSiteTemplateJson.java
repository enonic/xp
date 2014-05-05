package com.enonic.wem.admin.rest.resource.content.site.template.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

import com.enonic.wem.api.schema.content.ContentTypeFilterJson;
import com.enonic.wem.api.content.site.CreateSiteTemplateParams;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class CreateSiteTemplateJson
{
    final CreateSiteTemplateParams createSiteTemplate;

    @JsonCreator
    CreateSiteTemplateJson( @JsonProperty("displayName") final String displayName, @JsonProperty("description") final String description,
                            @JsonProperty("url") final String url, @JsonProperty("vendor") final VendorJson vendorJson,
                            @JsonProperty("moduleKeys") List<String> moduleKeys,
                            @JsonProperty("contentTypeFilter") final ContentTypeFilterJson filterJson,
                            @JsonProperty("rootContentType") final String rootContentType )
    {
        this.createSiteTemplate = new CreateSiteTemplateParams().
            displayName( displayName ).
            description( description ).
            url( url ).
            vendor( vendorJson.toVendor() ).
            modules( ModuleKeys.from( Iterables.toArray( moduleKeys, String.class ) ) ).
            contentTypeFilter( filterJson.toContentTypeFilter() ).
            rootContentType( ContentTypeName.from( rootContentType ) );
    }

    @JsonIgnore
    public CreateSiteTemplateParams getCommand()
    {
        return createSiteTemplate;
    }
}
