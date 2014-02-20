package com.enonic.wem.admin.rest.resource.content.site.template.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Iterables;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;

public class CreateSiteTemplateJson
{
    final CreateSiteTemplate createSiteTemplate;

    @JsonCreator
    CreateSiteTemplateJson( @JsonProperty("displayName") final String displayName,
                            @JsonProperty("description") final String description,
                            @JsonProperty("url") final String url,
                            @JsonProperty("vendor") final VendorJson vendorJson,
                            @JsonProperty("moduleKeys") List<String> moduleKeys,
                            @JsonProperty("contentTypeFilter") final ContentTypeFilterJson filterJson,
                            @JsonProperty("rootContentType") final String rootContentType )
    {
        this.createSiteTemplate = Commands.site().template().create().
            displayName( displayName ).
            description( description ).
            url( url ).
            vendor( vendorJson.toVendor() ).
            modules( ModuleKeys.from( Iterables.toArray( moduleKeys, String.class ) ) ).
            contentTypeFilter( filterJson.toContentTypeFilter() ).
            rootContentType( ContentTypeName.from( rootContentType ) );
    }

    @JsonIgnore
    public CreateSiteTemplate getCommand()
    {
        return createSiteTemplate;
    }
}
