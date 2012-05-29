package com.enonic.wem.taglib;

public interface JspHelper
{
    public String getProductVersion();

    public String getBaseUrl();

    public String createUrl( String path );

    public String ellipsis( String text, int length );

    public <T> T getBean( Class<T> type );
}
