package com.enonic.xp.launcher.impl.provision;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.launcher.impl.config.ConfigProperties;

final class BundleInfoLoader
{
    private final DocumentBuilderFactory documentBuilderFactory;

    private final File systemDir;

    private final BundleLocationResolver resolver;

    public BundleInfoLoader( final File systemDir, final ConfigProperties config )
    {
        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
        this.systemDir = systemDir;
        this.resolver = new BundleLocationResolver( this.systemDir, config );
    }

    public List<BundleInfo> load()
        throws Exception
    {
        final Set<BundleInfo> set = Sets.newHashSet();
        for ( final File file : findBundleFiles() )
        {
            loadBundles( set, file );
        }

        final List<BundleInfo> list = Lists.newArrayList( set );
        Collections.sort( list );
        return list;
    }

    private File[] findBundleFiles()
    {
        final File[] files = this.systemDir.listFiles( this::isBundleFile );
        return files != null ? files : new File[0];
    }

    private boolean isBundleFile( final File file )
    {
        return file.isFile() && file.getName().endsWith( ".xml" );
    }

    private void loadBundles( final Set<BundleInfo> set, final File file )
        throws Exception
    {
        final DocumentBuilder builder = this.documentBuilderFactory.newDocumentBuilder();
        final Document doc = builder.parse( file );
        build( set, doc.getDocumentElement() );
    }

    private List<Element> findElements( final Element root, final String name )
    {
        final List<Element> result = Lists.newArrayList();

        final NodeList list = root.getChildNodes();
        for ( int i = 0; i < list.getLength(); i++ )
        {
            final Node node = list.item( i );
            if ( ( node instanceof Element ) && node.getNodeName().equals( name ) )
            {
                result.add( (Element) node );
            }
        }

        return result;
    }

    private void build( final Collection<BundleInfo> list, final Element root )
    {
        list.addAll( findElements( root, "bundle" ).stream().map( this::buildItem ).collect( Collectors.toList() ) );
    }

    private BundleInfo buildItem( final Element item )
    {
        final String gav = item.getTextContent().trim();
        final String level = item.getAttribute( "level" );

        final String file = this.resolver.resolve( gav );
        return new BundleInfo( file, Integer.parseInt( level ) );
    }
}
