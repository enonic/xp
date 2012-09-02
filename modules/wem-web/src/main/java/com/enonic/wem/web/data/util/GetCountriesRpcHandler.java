package com.enonic.wem.web.data.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.rest2.resource.country.CountryResource;
import com.enonic.wem.web.rpc.JsonRpcContext;

@Component
public final class GetCountriesRpcHandler
    extends AbstractDataRpcHandler
{
    @Autowired
    private CountryResource resource;

    public GetCountriesRpcHandler()
    {
        super( "util_getCountries" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final JsonSerializable json = this.resource.getAll();
        context.setResult( json );
    }
}
