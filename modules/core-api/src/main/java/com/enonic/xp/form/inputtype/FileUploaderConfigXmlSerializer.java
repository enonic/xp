package com.enonic.xp.form.inputtype;

import org.w3c.dom.Element;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;

final class FileUploaderConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<FileUploaderConfig>
{
    public static final FileUploaderConfigXmlSerializer DEFAULT = new FileUploaderConfigXmlSerializer();

    @Override
    protected void serializeConfig( final FileUploaderConfig config, final DomBuilder builder )
    {
        final ImmutableSet<String> allowTypeNames = config.getAllowTypeNames();
        if ( !allowTypeNames.isEmpty() )
        {
            builder.start( "allow-types" );

            for ( String allowTypeName : allowTypeNames )
            {
                builder.start( "type" );
                builder.attribute( "name", allowTypeName );
                builder.text( config.getAllowTypeExtensions( allowTypeName ) );
                builder.end();
            }

            builder.end();
        }

        if ( config.hideDropZone() )
        {
            builder.start( "hide-drop-zone" );
            builder.text( "true" );
            builder.end();
        }
    }

    @Override
    public FileUploaderConfig parseConfig( final ApplicationKey currentApp, final Element elem )
    {
        final FileUploaderConfig.Builder builder = FileUploaderConfig.create();
        final Element allowTypesEl = DomHelper.getChildElementByTagName( elem, "allow-types" );

        if ( allowTypesEl != null )
        {
            for ( final Element typeEl : DomHelper.getChildElementsByTagName( allowTypesEl, "type" ) )
            {
                final String extensions = typeEl.getTextContent();
                final String name = typeEl.getAttribute( "name" );
                builder.allowType( name, extensions );
            }
        }

        final Element hideDropZoneEl = DomHelper.getChildElementByTagName( elem, "hide-drop-zone" );
        if ( hideDropZoneEl != null )
        {
            builder.hideDropZone( "true".equals( hideDropZoneEl.getTextContent() ) );
        }

        return builder.build();
    }

}
