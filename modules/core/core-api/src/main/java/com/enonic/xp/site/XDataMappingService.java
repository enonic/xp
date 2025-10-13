package com.enonic.xp.site;

import java.util.List;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.schema.content.ContentTypeName;

public interface XDataMappingService
{
    List<XDataOption> fetch( ContentPath path, ContentTypeName type );

    List<XDataOption> fetch( final SiteConfigs configs, final ContentTypeName type );
}
