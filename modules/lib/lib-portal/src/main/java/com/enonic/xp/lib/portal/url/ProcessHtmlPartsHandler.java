package com.enonic.xp.lib.portal.url;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.portal.url.PortalScope;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlPartsParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.serializer.MapSerializable;

/**
 * Backs {@code processHtmlParts} of {@code /lib/xp/portal}.
 */
public final class ProcessHtmlPartsHandler
    implements ScriptBean
{
    private Supplier<PortalUrlService> urlServiceSupplier;

    private String value;

    private PortalScope scope;

    private List<Integer> imageWidths;

    private String imageSizes;

    @Override
    public void initialize( final BeanContext context )
    {
        this.urlServiceSupplier = context.getService( PortalUrlService.class );
    }

    public void setValue( final String value )
    {
        this.value = value;
    }

    public void setScope( final PortalScope scope )
    {
        this.scope = scope;
    }

    public void setImageWidths( final List<Integer> imageWidths )
    {
        this.imageWidths = imageWidths;
    }

    public void setImageSizes( final String imageSizes )
    {
        this.imageSizes = imageSizes;
    }

    public MapSerializable process()
    {
        return new ProcessedHtmlMapper( urlServiceSupplier.get()
                                            .processHtmlParts( ProcessHtmlPartsParams.create()
                                                                   .value( this.value )
                                                                   .scope( this.scope )
                                                                   .imageWidths( this.imageWidths )
                                                                   .imageSizes( this.imageSizes )
                                                                   .build() ) );
    }
}
