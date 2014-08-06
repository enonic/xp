package com.enonic.wem.api.xml.mapper;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.xml.model.XmlContentFilter;
import com.enonic.wem.api.xml.model.XmlModules;
import com.enonic.wem.api.xml.model.XmlSiteTemplate;
import com.enonic.wem.api.xml.model.XmlVendor;

public class XmlSiteTemplateMapper
{
    private static final QName ALLOW_QNAME = new QName( "allow" );

    private static final QName DENY_QNAME = new QName( "deny" );

    public static XmlSiteTemplate toXml( final SiteTemplate object )
    {
        XmlSiteTemplate result = new XmlSiteTemplate();
        result.setDisplayName( object.getDisplayName() );
        result.setDescription( object.getDescription() );
        result.setUrl( object.getUrl() );
        result.setVendor( toXml( object.getVendor() ) );
        result.setModules( toXml( object.getModules() ) );
        result.setContentFilter( toXml( object.getContentTypeFilter() ) );
        return result;
    }

    public static void fromXml( final XmlSiteTemplate xml, final SiteTemplate.Builder builder )
    {
        builder.displayName( xml.getDisplayName() );
        builder.description( xml.getDescription() );
        builder.url( xml.getUrl() );
        builder.vendor( fromXml( xml.getVendor() ) );
        builder.modules( fromXml( xml.getModules() ) );
        builder.contentTypeFilter( fromXml( xml.getContentFilter() ) );
    }


    private static XmlVendor toXml( final Vendor vendor )
    {
        XmlVendor result = new XmlVendor();
        result.setName( vendor.getName() );
        result.setUrl( vendor.getUrl() );
        return result;
    }

    private static XmlModules toXml( final ModuleKeys modules )
    {
        XmlModules result = new XmlModules();
        for ( ModuleKey module : modules )
        {
            result.getList().add( module.toString() );
        }
        return result;
    }

    private static XmlContentFilter toXml( final ContentTypeFilter contentTypeFilter )
    {
        XmlContentFilter filter = new XmlContentFilter();

        if ( contentTypeFilter.getDefaultAccess() == ContentTypeFilter.AccessType.ALLOW )
        {
            filter.getDenyOrAllow().add( new JAXBElement( ALLOW_QNAME, String.class, "*" ) );
        }
        else
        {
            filter.getDenyOrAllow().add( new JAXBElement( DENY_QNAME, String.class, "*" ) );
        }

        for ( ContentTypeName cty : contentTypeFilter )
        {
            JAXBElement<String> element;
            if ( contentTypeFilter.isContentTypeAllowed( ContentTypeName.from( cty.getContentTypeName() ) ) )
            {
                element = new JAXBElement( ALLOW_QNAME, String.class, cty.getContentTypeName() );
            }
            else
            {
                element = new JAXBElement( DENY_QNAME, String.class, cty.getContentTypeName() );
            }
            filter.getDenyOrAllow().add( element );
        }
        return filter;
    }

    private static Vendor fromXml( final XmlVendor vendor )
    {
        final Vendor.Builder builder = Vendor.newVendor();

        if ( vendor != null )
        {
            builder.name( vendor.getName() );
            builder.url( vendor.getUrl() );
        }

        return builder.build();
    }

    private static ModuleKeys fromXml( final XmlModules modules )
    {
        if ( modules != null )
        {
            String[] moduleKeys = new String[modules.getList().size()];
            modules.getList().toArray( moduleKeys );
            return ModuleKeys.from( moduleKeys );
        }

        return ModuleKeys.empty();
    }

    private static ContentTypeFilter fromXml( final XmlContentFilter contentFilter )
    {
        ContentTypeFilter.Builder builder = ContentTypeFilter.newContentFilter();
        List<JAXBElement<String>> filterElements = contentFilter.getDenyOrAllow();

        int defaultElement = -1;
        for ( int i = 0; i < filterElements.size(); i++ )
        {
            if ( filterElements.get( i ).getValue().equals( "*" ) )
            {
                defaultElement = i;
                break;
            }
        }

        if ( defaultElement == -1 )
        {
            builder.defaultDeny();
//            throw new XmlException( "Content type filter has no default element" );
        }
        else
        {
            if ( filterElements.get( defaultElement ).getName().getLocalPart().equalsIgnoreCase( "allow" ) )
            {
                builder.defaultAllow();
            }
            else
            {
                builder.defaultDeny();
            }
        }

        for ( int i = 0; i < filterElements.size(); i++ )
        {
            if ( i == defaultElement )
            {
                continue;
            }

            if ( filterElements.get( i ).getName().getLocalPart().equalsIgnoreCase( "allow" ) )
            {
                builder.allowContentType( filterElements.get( i ).getValue() );
            }
            else
            {
                builder.denyContentType( filterElements.get( i ).getValue() );
            }
        }

        return builder.build();
    }
}
