package com.enonic.wem.api.content.site;

import java.text.MessageFormat;

import com.enonic.wem.api.exception.BaseException;

public class NoSiteTemplateExistsException extends BaseException
{

    public NoSiteTemplateExistsException(SiteTemplateKey key) {
        super( MessageFormat.format( "Site template with key [{0}] was not found", key.toString() ));
    }
}
