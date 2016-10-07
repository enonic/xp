package com.enonic.xp.admin.impl.market;

import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.application.json.MarketApplicationsJson;

public interface MarketDataProvider
{

    MarketApplicationsJson search( List<String> ids, String version, int start, int count );

}
