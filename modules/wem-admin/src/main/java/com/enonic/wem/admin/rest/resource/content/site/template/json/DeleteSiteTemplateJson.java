package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class DeleteSiteTemplateJson extends ResultJson<String>
{
    protected DeleteSiteTemplateJson( final String result, final ErrorJson error )
    {
        super( result, error );
    }

    public static DeleteSiteTemplateJson error(String error) {
        return new DeleteSiteTemplateJson( null, new ErrorJson( error ) );
    }

    public static DeleteSiteTemplateJson result(SiteTemplateKey key) {
        return new DeleteSiteTemplateJson( key.toString(), null );
    }
}
