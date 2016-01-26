package com.enonic.xp.admin.impl.market;

public @interface MarketConfig
{
    String marketUrl() default "https://enonic.com/market/applications";
}
