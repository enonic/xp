package com.enonic.wem.admin.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.api.command.content.page.CreatePage;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.data.RootDataSet;

public class CreatePageParams
{
    private final CreatePage createPage;

    @JsonCreator
    public CreatePageParams( @JsonProperty("content") String content, @JsonProperty("pageTemplate") String pageTemplate,
                             @JsonProperty("config") List<DataJson> config )
    {
        this.createPage = new CreatePage().
            content( ContentId.from( content ) ).
            pageTemplate( new PageTemplateName( pageTemplate ) ).
            config( parseData( config ) );
    }

    @JsonIgnore
    public CreatePage getCreatePage()
    {
        return createPage;
    }

    private static RootDataSet parseData( final List<DataJson> dataJsonList )
    {
        final RootDataSet data = new RootDataSet();
        for ( DataJson dataJson : dataJsonList )
        {
            data.add( dataJson.getData() );
        }
        return data;
    }
}
