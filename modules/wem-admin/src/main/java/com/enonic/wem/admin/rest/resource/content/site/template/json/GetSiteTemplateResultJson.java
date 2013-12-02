package com.enonic.wem.admin.rest.resource.content.site.template.json;


import com.enonic.wem.admin.json.content.site.SiteTemplateSummaryJson;
import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;

public class GetSiteTemplateResultJson
    extends ResultJson<SiteTemplateSummaryJson>
{

    private GetSiteTemplateResultJson( final SiteTemplateSummaryJson siteTemplateJson, final ErrorJson error )
    {
        super(siteTemplateJson, error);
    }

    public static GetSiteTemplateResultJson error( final String message )
    {
        return new GetSiteTemplateResultJson( null, new ErrorJson( message ) );
    }

    public static GetSiteTemplateResultJson result( final SiteTemplateSummaryJson siteTemplateJson )
    {
        return new GetSiteTemplateResultJson( siteTemplateJson, null );
    }
}
