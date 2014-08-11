package com.enonic.wem.api.xml.mapper;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.JDOMException;
import org.jdom2.input.DOMBuilder;
import org.jdom2.output.DOMOutputter;
import org.w3c.dom.Element;

import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.serializer.DataXmlSerializer;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.xml.model.XmlCanRenderDescriptor;
import com.enonic.wem.api.xml.model.XmlPageRegion;
import com.enonic.wem.api.xml.model.XmlPageRegions;
import com.enonic.wem.api.xml.model.XmlPageTemplate;
import com.enonic.wem.api.xml.model.XmlPartComponent;

public class XmlPageTemplateMapper
{

    public static XmlPageTemplate toXml( final PageTemplate object )
        throws JDOMException
    {
        XmlPageTemplate result = new XmlPageTemplate();
        result.setDisplayName( object.getDisplayName() );
        result.setDescriptor( object.getDescriptor().toString() );
        result.setConfig( toConfigXml( object.getConfig() ) );
        result.setCanRender( toCanRenderXml( object.getCanRender() ) );
        result.setRegions( toRegionsXml( object.getRegions() ) );
        return result;
    }

    public static void fromXml( final XmlPageTemplate xml, final PageTemplate.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.descriptor( PageDescriptorKey.from( xml.getDescriptor() ) );
        builder.config( fromConfigXml( xml.getConfig() ) );
        builder.canRender( ContentTypeNames.from( xml.getCanRender().getContentTypes() ) );
        builder.regions( fromRegionsXml( xml.getRegions() ) );
    }

    private static Element toConfigXml( final DataSet dataSet )
        throws JDOMException
    {
        final org.jdom2.Element dataEl = new org.jdom2.Element( "config" );
        new DataXmlSerializer().generateRootDataSet( dataEl, dataSet.toRootDataSet() );
        return new DOMOutputter().output( dataEl );
    }

    private static XmlCanRenderDescriptor toCanRenderXml( final ContentTypeNames canRenderCtys )
    {
        XmlCanRenderDescriptor result = new XmlCanRenderDescriptor();
        for ( ContentTypeName name : canRenderCtys )
        {
            result.getContentTypes().add( name.getContentTypeName() );
        }
        return result;
    }

    private static XmlPageRegions toRegionsXml( final PageRegions object )
        throws JDOMException
    {
        XmlPageRegions xml = new XmlPageRegions();
        xml.getList().addAll( toRegionXml( object ) );
        return xml;
    }

    private static List<XmlPageRegion> toRegionXml( final PageRegions object )
        throws JDOMException
    {
        List<XmlPageRegion> pageRegionList = new ArrayList<>();
        for ( Region region : object )
        {
            pageRegionList.add( toPartComponentsXml( region ) );
        }
        return pageRegionList;
    }

    private static XmlPageRegion toPartComponentsXml( final Region region )
        throws JDOMException
    {
        XmlPageRegion result = new XmlPageRegion();
        result.setName( region.getName() );
        for ( PageComponent pageComponent : region.getComponents() )
        {
            result.getPartComponent().add( toPageComponentXml( pageComponent ) );
        }
        return result;
    }

    private static XmlPartComponent toPageComponentXml( final PageComponent pageComponent )
        throws JDOMException
    {
        PartComponent partComponent = (PartComponent) pageComponent;
        XmlPartComponent result = new XmlPartComponent();
        result.setName( partComponent.getName().toString() );
        result.setDescriptor( partComponent.getDescriptor().toString() );
        result.setConfig( toConfigXml( partComponent.getConfig() ) );
        return result;
    }

    private static RootDataSet fromConfigXml( final Object object )
    {
        final Element element = Element.class.cast( object );
        final org.jdom2.Element dataEl = new DOMBuilder().build( element );
        return new DataXmlSerializer().parse( dataEl );
    }

    private static PageRegions fromRegionsXml( final XmlPageRegions regions )
    {
        PageRegions.Builder builder = PageRegions.newPageRegions();
        for ( XmlPageRegion region : regions.getList() )
        {
            builder.add( fromRegionXml( region ) );
        }
        return builder.build();
    }

    private static Region fromRegionXml( final XmlPageRegion region )
    {
        Region.Builder builder = Region.newRegion();
        builder.name( region.getName() );
        for ( XmlPartComponent partComponenet : region.getPartComponent() )
        {
            builder.add( fromPartComponentXml( partComponenet ) );
        }
        return builder.build();
    }

    private static PartComponent fromPartComponentXml( final XmlPartComponent partComponenet )
    {
        PartComponent.Builder builder = PartComponent.newPartComponent();
        builder.name( partComponenet.getName() );
        builder.descriptor( partComponenet.getDescriptor() );
        builder.config( fromConfigXml( partComponenet.getConfig() ) );
        return builder.build();
    }

}
