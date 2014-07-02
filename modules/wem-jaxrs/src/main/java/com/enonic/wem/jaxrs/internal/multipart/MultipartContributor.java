package com.enonic.wem.jaxrs.internal.multipart;

import java.util.Collections;
import java.util.List;

import com.enonic.wem.jaxrs.JaxRsContributor;

public final class MultipartContributor
    implements JaxRsContributor
{
    @Override
    public List<Object> getObjects()
    {
        return Collections.singletonList( new MultipartFormReader() );
    }
}
