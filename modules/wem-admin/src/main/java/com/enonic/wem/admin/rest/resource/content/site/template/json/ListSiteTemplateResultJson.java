package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;

public class ListSiteTemplateResultJson
    extends ResultJson<ListSiteTemplateJson>
{
    private ListSiteTemplateResultJson(ListSiteTemplateJson result, ErrorJson error)
    {
        super(result, error);
    }

    public static ListSiteTemplateResultJson error( String message )
    {
        return new ListSiteTemplateResultJson( null, new ErrorJson( message ) );
    }

    public static ListSiteTemplateResultJson result( ListSiteTemplateJson listSiteTemplateJson )
    {
        return new ListSiteTemplateResultJson( listSiteTemplateJson, null );
    }
}
