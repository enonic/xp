package com.enonic.wem.portal.view;

public interface ViewFunctions
{
    public String url( String... params );

    public String assetUrl( String... params );

    public String pageUrl( String... params );

    public String imageUrl( String... params );

    public String attachmentUrl( String... params );

    public String serviceUrl( String... params );

    public String componentUrl( String... params );
}
