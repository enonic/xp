package com.enonic.xp.admin.impl.market;

import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;

public interface MarketService
{
    MarketApplicationsJson get( final String version );
}
