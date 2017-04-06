package com.enonic.xp.core.impl.media;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.media.MediaTypeProvider;
import com.enonic.xp.util.MediaTypes;

@Component(immediate = true)
public final class MediaTypeProviderRegister
{
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final MediaTypeProvider provider )
    {
        MediaTypes.instance().addProvider( provider );
    }

    public void removeProvider( final MediaTypeProvider provider )
    {
        MediaTypes.instance().removeProvider( provider );
    }
}
