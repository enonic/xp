package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.site.CmsDescriptor;

public interface DynamicSchemaServiceInternal
{
    DynamicSchemaResult<CmsDescriptor> createCms( CreateDynamicCmsParams params );

    boolean deleteCms( ApplicationKey applicationKey );
}
