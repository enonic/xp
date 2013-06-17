package com.enonic.wem.core.resource.dao;

import com.enonic.wem.api.resource.Resource;

public interface ResourceDao
{

    public Resource getResource( String path, String module );


}
