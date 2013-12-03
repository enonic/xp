package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.enonic.wem.admin.json.content.site.SiteTemplateSummaryJson;
import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;


public class ImportSiteTemplateResultJson extends ResultJson<SiteTemplateSummaryJson>
{
    protected ImportSiteTemplateResultJson( final SiteTemplateSummaryJson result, final ErrorJson error )
    {
        super( result, error );
    }

   public static ImportSiteTemplateResultJson error( final String message )
    {
        return new ImportSiteTemplateResultJson( null, new ErrorJson( message ) );
    }

    public static ImportSiteTemplateResultJson result( final SiteTemplateSummaryJson moduleJson )
    {
        return new ImportSiteTemplateResultJson( moduleJson, null );
    }
}
