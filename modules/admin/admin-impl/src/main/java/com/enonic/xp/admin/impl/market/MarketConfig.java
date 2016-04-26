package com.enonic.xp.admin.impl.market;

public @interface MarketConfig
{
    String marketUrl() default "https://market.enonic.com/applications";
    String marketProxy() default "";
}
