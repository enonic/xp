package com.enonic.wem.admin.rest.resource.content.site.template.json;

import com.enonic.wem.admin.rest.ResultJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;
import com.enonic.wem.api.content.site.SiteTemplateKey;

public class DeleteSiteTemplateResultJson
    extends ResultJson<String>
{
    protected DeleteSiteTemplateResultJson( final String result, final ErrorJson error )
    {
        super( result, error );
    }

    public static DeleteSiteTemplateResultJson error(String error) {
        return new DeleteSiteTemplateResultJson( null, new ErrorJson( error ) );
    }

    public static DeleteSiteTemplateResultJson result(SiteTemplateKey key) {
        return new DeleteSiteTemplateResultJson( key.toString(), null );
    }
}
