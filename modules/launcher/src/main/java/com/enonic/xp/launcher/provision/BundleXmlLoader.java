package com.enonic.xp.launcher.provision;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;

final class BundleXmlLoader
{
    private final File file;

    public BundleXmlLoader( final File file )
    {
        this.file = file;
    }

    public List<BundleInfo> load()
        throws Exception
    {
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = builder.parse( this.file );

        final List<BundleInfo> list = Lists.newArrayList();
        build( list, doc );
        return list;
    }

    private void build( final List<BundleInfo> list, final Document doc )
    {
        build( list, doc.getDocumentElement() );
    }

    private void build( final List<BundleInfo> list, final Element root )
    {
        if ( !root.getNodeName().equals( "bundles" ) )
        {
            return;
        }

        final NodeList children = root.getChildNodes();
        for ( int i = 0; i < children.getLength(); i++ )
        {
            final Node node = children.item( i );
            if ( node instanceof Element )
            {
                buildItem( list, (Element) node );
            }
        }
    }

    private void buildItem( final List<BundleInfo> list, final Element item )
    {
        if ( !item.getNodeName().equals( "bundle" ) )
        {
            return;
        }

        final String location = item.getTextContent().trim();
        final int level = getIntAttribute( item, "level", 1 );
        list.add( new BundleInfo( location, level ) );
    }

    private int getIntAttribute( final Element elem, final String name, final int defValue )
    {
        final String value = elem.getAttribute( name );
        if ( value == null )
        {
            return defValue;
        }

        try
        {
            return Integer.parseInt( value );
        }
        catch ( final Exception e )
        {
            return defValue;
        }
    }
}
