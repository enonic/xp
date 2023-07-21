package com.enonic.xp.portal.impl.rendering;

import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutDescriptorService;

@Component
public final class RendererDelegateImpl
    implements RendererDelegate
{
    private final CopyOnWriteArrayList<Renderer<? super Object>> renderers = new CopyOnWriteArrayList<>();

    private final ContentService contentService;

    private final LayoutDescriptorService layoutDescriptorService;

    @Activate
    public RendererDelegateImpl( @Reference final ContentService contentService,
                                 @Reference final LayoutDescriptorService layoutDescriptorService )
    {
        this.contentService = contentService;
        this.layoutDescriptorService = layoutDescriptorService;
    }

    @Override
    public PortalResponse render( Object renderable, PortalRequest portalRequest )
    {
        if ( renderable instanceof FragmentComponent )
        {
            return new FragmentRenderer( contentService, layoutDescriptorService, this ).render( (FragmentComponent) renderable, portalRequest );
        }
        return renderers.stream().
            filter( r -> r.getType().isInstance( renderable ) ).
            findAny().
            map( r -> r.render( renderable, portalRequest ) ).
            orElseThrow( () -> new RendererNotFoundException( renderable.getClass() ) );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addRenderer( final Renderer<Object> renderer )
    {
        this.renderers.add( renderer );
    }

    public void removeRenderer( final Renderer<Object> renderer )
    {
        this.renderers.remove( renderer );
    }

}
