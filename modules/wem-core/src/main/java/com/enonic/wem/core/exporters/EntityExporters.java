package com.enonic.wem.core.exporters;

import java.util.HashMap;
import java.util.Map;

import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.Module;

public class EntityExporters
{
    private final static Map<Class, AbstractEntityExporter> classToExporterMap = new HashMap<>();

    public static <T> AbstractEntityExporter<T> getForObject( T object )
    {
        return getForClass( object.getClass() );
    }

    public static <T> AbstractEntityExporter<T> getForClass( Class clazz )
    {
        return classToExporterMap.get( clazz );
    }

    protected static <T> AbstractEntityExporter<T> getByFilename( final String filename )
    {
        for ( final AbstractEntityExporter entityExporter : classToExporterMap.values() )
        {
            if ( filename.equals( entityExporter.getXmlFileName() ) )
            {
                return entityExporter;
            }
        }
        return null;
    }

    private static void registerExporter( Class<?> clazz, AbstractEntityExporter abstractExporter )
    {
        classToExporterMap.put( clazz, abstractExporter );
    }

    static
    {
        registerExporter( Module.class, new ModuleExporter() );
        registerExporter( SiteTemplate.class, new SiteTemplateExporter() );

        registerExporter( ImageTemplate.class, new ImageTemplateExporter() );
        registerExporter( LayoutTemplate.class, new LayoutTemplateExporter() );
        registerExporter( PageTemplate.class, new PageTemplateExporter() );
        registerExporter( PartTemplate.class, new PartTemplateExporter() );
    }

}
