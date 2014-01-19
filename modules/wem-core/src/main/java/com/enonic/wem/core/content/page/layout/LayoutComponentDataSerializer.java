package com.enonic.wem.core.content.page.layout;


import com.enonic.wem.api.content.page.TemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.core.content.page.PageComponentDataSerializer;

public class LayoutComponentDataSerializer
    extends PageComponentDataSerializer<LayoutComponent, LayoutComponent>
{
    public DataSet toData( final LayoutComponent component )
    {
        final DataSet asData = new DataSet( LayoutComponent.class.getSimpleName() );
        applyPageComponentToData( component, asData );
        return asData;
    }

    public LayoutComponent fromData( final DataSet asData )
    {
        LayoutComponent.Builder component = LayoutComponent.newLayoutComponent();
        applyPageComponentFromData( component, asData );
        return component.build();
    }

    @Override
    protected TemplateKey toTemplatekey( final String s )
    {
        return LayoutTemplateKey.from( s );
    }

}
