package com.enonic.wem.admin.rest.resource.content.page;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.api.command.content.page.UpdatePage;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageEditor;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.data.RootDataSet;

import static com.enonic.wem.api.content.page.Page.editPage;

public class UpdatePageJson
{
    private final UpdatePage updatePage;

    @JsonCreator
    public UpdatePageJson( @JsonProperty("contentId") final String contentId,
                           @JsonProperty("pageTemplateKey") final String pageTemplateKey,
                           @JsonProperty("config") final List<DataJson> config )
    {
        this.updatePage = new UpdatePage().
            content( ContentId.from( contentId ) ).
            editor( new PageEditor()
            {
                @Override
                public Page.PageEditBuilder edit( final Page toBeEdited )
                {
                    return editPage( toBeEdited ).
                        template( PageTemplateKey.from( pageTemplateKey ) ).
                        config( parseData( config ) );
                }
            } );
    }

    @JsonIgnore
    public UpdatePage getUpdatePage()
    {
        return updatePage;
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
