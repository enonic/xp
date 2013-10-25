package com.enonic.wem.core.rendering;


import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.core.rendering.ComponentType;
import com.enonic.wem.core.rendering.Context;
import com.enonic.wem.core.rendering.RenderingResult;

public class PartComponentType
    implements ComponentType<Part>
{
    @Override
    public RenderingResult execute( final Part part, final Context context )
    {
        return null;
    }

}
