package com.enonic.wem.core.support.export;

import java.util.HashMap;
import java.util.Map;

import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.page.PageTemplateExporter;
import com.enonic.wem.core.content.page.image.ImageTemplateExporter;
import com.enonic.wem.core.content.page.layout.LayoutTemplateExporter;
import com.enonic.wem.core.content.page.part.PartTemplateExporter;
import com.enonic.wem.core.content.site.SiteTemplateExporter;
import com.enonic.wem.core.module.ModuleExporter;

public class EntityExporters
{
    private final static Map<Class, AbstractEntityExporter> classToExporterMap = new HashMap<>();

    public static <I, O> AbstractEntityExporter<I, O> getForObject( I object )
    {
        return getForClass( object.getClass() );
    }

    public static <I, O> AbstractEntityExporter<I, O> getForClass( Class clazz )
    {
        return classToExporterMap.get( clazz );
    }

    public static <I, O> AbstractEntityExporter<I, O> getByFilename( final String filename )
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
