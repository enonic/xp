package com.enonic.xp.site;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.schema.content.ContentTypeName;

public interface XDataMappingService
{
    XDataOptions fetch( final ContentTypeName type, ApplicationKeys applicationKeys );
}
