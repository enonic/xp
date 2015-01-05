package com.enonic.xp.web;

public interface WebHandler
{
    public final static int MIN_ORDER = Integer.MIN_VALUE;

    public final static int MAX_ORDER = Integer.MAX_VALUE;

    public int getOrder();

    public boolean handle( WebContext context )
        throws Exception;
}
