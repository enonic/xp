package com.enonic.xp.lib.thymeleaf;

import java.util.List;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;

final class ThymeleafViewFunctions
{
    protected ViewFunctionService viewFunctionService;

    protected PortalRequest portalRequest;

    public String assetUrl( final List<String> params )
    {
        return execute( "assetUrl", params ).toString();
    }

    public String pageUrl( final List<String> params )
    {
        return execute( "pageUrl", params ).toString();
    }

    public String attachmentUrl( final List<String> params )
    {
        return execute( "attachmentUrl", params ).toString();
    }

    public String componentUrl( final List<String> params )
    {
        return execute( "componentUrl", params ).toString();
    }

    public String imageUrl( final List<String> params )
    {
        return execute( "imageUrl", params ).toString();
    }

    public String serviceUrl( final List<String> params )
    {
        return execute( "serviceUrl", params ).toString();
    }

    public String idProviderUrl( final List<String> params )
    {
        return execute( "idProviderUrl", params ).toString();
    }

    public String loginUrl( final List<String> params )
    {
        return execute( "loginUrl", params ).toString();
    }

    public String logoutUrl( final List<String> params )
    {
        return execute( "logoutUrl", params ).toString();
    }

    public String imagePlaceholder( final List<String> params )
    {
        return execute( "imagePlaceholder", params ).toString();
    }

    public String processHtml( final List<String> params )
    {
        return execute( "processHtml", params ).toString();
    }

    public String localize( final List<String> params )
    {
        return execute( "i18n.localize", params ).toString();
    }

    private Object execute( final String name, final List<String> args )
    {
        final ViewFunctionParams params = new ViewFunctionParams().name( name ).args( args ).portalRequest( this.portalRequest );
        return this.viewFunctionService.execute( params );
    }
}
