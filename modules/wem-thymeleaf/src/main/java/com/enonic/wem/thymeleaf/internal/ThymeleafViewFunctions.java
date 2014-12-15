package com.enonic.wem.thymeleaf.internal;

import java.util.List;

import com.enonic.wem.portal.view.ViewFunctions;
import com.enonic.wem.portal.view.ViewHelper;

final class ThymeleafViewFunctions
{
    private final ViewFunctions functions;

    public ThymeleafViewFunctions( final ViewFunctions functions )
    {
        this.functions = functions;
    }

    public String url( final List<String> params )
    {
        return this.functions.url( ViewHelper.toParamMap( params ) );
    }

    public String assetUrl( final List<String> params )
    {
        return this.functions.assetUrl( ViewHelper.toParamMap( params ) );
    }

    public String pageUrl( final List<String> params )
    {
        return this.functions.pageUrl( ViewHelper.toParamMap( params ) );
    }

    public String attachmentUrl( final List<String> params )
    {
        return this.functions.attachmentUrl( ViewHelper.toParamMap( params ) );
    }

    public String componentUrl( final List<String> params )
    {
        return this.functions.componentUrl( ViewHelper.toParamMap( params ) );
    }

    public String imageUrl( final List<String> params )
    {
        return this.functions.imageUrl( ViewHelper.toParamMap( params ) );
    }

    public String serviceUrl( final List<String> params )
    {
        return this.functions.serviceUrl( ViewHelper.toParamMap( params ) );
    }
}
