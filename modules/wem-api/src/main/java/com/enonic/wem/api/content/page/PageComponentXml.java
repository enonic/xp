package com.enonic.wem.api.content.page;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.image.ImageComponentXml;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponentXml;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.part.PartComponentXml;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.RootDataSetXml;


public abstract class PageComponentXml
{
    @XmlAttribute(name = "name", required = true)
    String name;

    @XmlAttribute(name = "descriptor", required = false)
    String descriptor;

    @XmlElement(name = "config", required = true)
    private RootDataSetXml config;

    public void from( final PageComponent partComponent )
    {
        this.name = partComponent.getName().toString();
        this.descriptor = partComponent.getDescriptor().toString();
        this.config = new RootDataSetXml();
        this.config.from( partComponent.getConfig() );
    }

    public void to( final PageComponent.Builder builder )
    {
        builder.name( new ComponentName( this.name ) );
        builder.descriptor( toDescriptorKey( this.descriptor ) );

        RootDataSet config = new RootDataSet();
        if ( this.config != null )
        {
            this.config.to( config );
        }
        builder.config( config );
    }

    protected abstract DescriptorKey toDescriptorKey( String s );

    public static PageComponent fromXml( final PageComponentXml componentXml )
    {
        if ( componentXml instanceof ImageComponentXml )
        {
            ImageComponentXml imageComponentXml = (ImageComponentXml) componentXml;
            ImageComponent.Builder imageComponent = ImageComponent.newImageComponent();
            imageComponentXml.to( imageComponent );
            return imageComponent.build();
        }
        else if ( componentXml instanceof PartComponentXml )
        {
            PartComponentXml partComponentXml = (PartComponentXml) componentXml;
            PartComponent.Builder partComponent = PartComponent.newPartComponent();
            partComponentXml.to( partComponent );
            return partComponent.build();
        }
        else if ( componentXml instanceof LayoutComponentXml )
        {
            LayoutComponentXml layoutComponentXml = (LayoutComponentXml) componentXml;
            LayoutComponent.Builder layoutComponent = LayoutComponent.newLayoutComponent();
            layoutComponentXml.to( layoutComponent );
            return layoutComponent.build();
        }
        else
        {
            throw new UnsupportedOperationException(
                "Creating PageComponent from [" + componentXml.getClass().getName() + "] not supported" );
        }
    }

    public static PageComponentXml toXml( final PageComponent component )
    {
        if ( component instanceof ImageComponent )
        {
            ImageComponentXml componentXml = new ImageComponentXml();
            componentXml.from( (ImageComponent) component );
            return componentXml;
        }
        else if ( component instanceof PartComponent )
        {
            PartComponentXml componentXml = new PartComponentXml();
            componentXml.from( (PartComponent) component );
            return componentXml;
        }
        else if ( component instanceof LayoutComponent )
        {
            LayoutComponentXml componentXml = new LayoutComponentXml();
            componentXml.from( (LayoutComponent) component );
            return componentXml;
        }
        else
        {
            throw new UnsupportedOperationException(
                "Creating PageComponentXml from [" + component.getClass().getName() + "] not supported" );
        }
    }
}
