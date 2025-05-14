package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ProcessHtmlHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String urlType;

    private String value;

    private List<Integer> imageWidths;

    private String imageSizes;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public ProcessHtmlHandler setUrlType( final String urlType )
    {
        this.urlType = urlType;
        return this;
    }

    public ProcessHtmlHandler setValue( final String value )
    {
        this.value = value;
        return this;
    }

    public ProcessHtmlHandler setImageWidths( final List<Integer> imageWidths )
    {
        this.imageWidths = imageWidths;
        return this;
    }

    public ProcessHtmlHandler setImageSizes( final String imageSizes )
    {
        this.imageSizes = imageSizes;
        return this;
    }

    public String createUrl()
    {
        final ProcessHtmlParams params =
            new ProcessHtmlParams().type( this.urlType ).value( this.value ).imageWidths( this.imageWidths ).imageSizes( this.imageSizes );

        return this.urlServiceSupplier.get().processHtml( params );
    }
}
