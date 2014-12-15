package com.enonic.wem.portal.view;

import com.google.common.collect.Multimap;

public interface ViewFunctions
{
    public String url( Multimap<String, String> params );

    public String assetUrl( Multimap<String, String> params );

    public String pageUrl( Multimap<String, String> params );

    public String attachmentUrl( Multimap<String, String> params );

    public String componentUrl( Multimap<String, String> params );

    public String imageUrl( Multimap<String, String> params );

    public String serviceUrl( Multimap<String, String> params );
}
