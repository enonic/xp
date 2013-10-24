package com.enonic.wem.api.content.page.rendering;


import com.enonic.wem.api.content.page.Part;
import com.enonic.wem.api.rendering.ComponentType;
import com.enonic.wem.api.rendering.Context;
import com.enonic.wem.api.rendering.RenderingResult;

public class PartComponentType
    implements ComponentType<Part>
{
    @Override
    public RenderingResult execute( final Part part, final Context context )
    {
        return null;
    }

}
