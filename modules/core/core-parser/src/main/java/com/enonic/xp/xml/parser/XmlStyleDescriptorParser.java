package com.enonic.xp.xml.parser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.style.ElementStyle;
import com.enonic.xp.style.GenericStyle;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.xml.DomElement;

public final class XmlStyleDescriptorParser
    extends XmlModelParser<XmlStyleDescriptorParser>
{
    private static final String ROOT_TAG_NAME = "styles";

    private static final String ROOT_CSS_ATTRIBUTE_NAME = "css";

    private static final String ELEMENT_NAME_ATTRIBUTE_NAME = "name";

    private static final String DISPLAY_NAME_TAG_NAME = "display-name";

    private static final String IMAGE_TAG_NAME = "image";

    private static final String STYLE_TAG_NAME = "style";

    private static final String ASPECT_RATIO_TAG_NAME = "aspect-ratio";

    private static final String FILTER_TAG_NAME = "filter";

    private static final String I18N_ATTRIBUTE_NAME = "i18n";

    private StyleDescriptor.Builder styleDescriptorBuilder;

    public XmlStyleDescriptorParser styleDescriptorBuilder( final StyleDescriptor.Builder styleDescriptorBuilder )
    {
        this.styleDescriptorBuilder = styleDescriptorBuilder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, ROOT_TAG_NAME );

        this.styleDescriptorBuilder.cssPath( root.getAttribute( ROOT_CSS_ATTRIBUTE_NAME ) );
        for ( ElementStyle styleElement : parseStyleElements( root ) )
        {
            this.styleDescriptorBuilder.addStyleElement( styleElement );
        }
    }

    private List<ElementStyle> parseStyleElements( final DomElement root )
    {
        return root.getChildren().stream().
            map( this::toStyleElement ).
            filter( Objects::nonNull ).
            collect( Collectors.toList() );
    }

    private ElementStyle toStyleElement( final DomElement styleElement )
    {
        final String elementName = styleElement.getTagName();
        if ( IMAGE_TAG_NAME.equals( elementName ) )
        {
            return toImageStyle( styleElement );
        }
        else if ( STYLE_TAG_NAME.equals( elementName ) )
        {
            return toGenericStyle( styleElement );
        }
        return null;
    }

    private ImageStyle toImageStyle( final DomElement styleElement )
    {
        final ImageStyle.Builder builder = ImageStyle.create();
        builder.name( styleElement.getAttribute( ELEMENT_NAME_ATTRIBUTE_NAME ) );

        final DomElement displayName = styleElement.getChild( DISPLAY_NAME_TAG_NAME );
        if ( displayName != null )
        {
            builder.displayName( displayName.getValue() );
            builder.displayNameI18nKey( displayName.getAttribute( I18N_ATTRIBUTE_NAME ) );
        }

        final DomElement aspectRatio = styleElement.getChild( ASPECT_RATIO_TAG_NAME );
        if ( aspectRatio != null )
        {
            builder.aspectRatio( aspectRatio.getValue().trim() );
        }

        final DomElement filter = styleElement.getChild( FILTER_TAG_NAME );
        if ( filter != null )
        {
            builder.filter( filter.getValue().trim() );
        }

        return builder.build();
    }

    private GenericStyle toGenericStyle( final DomElement styleElement )
    {
        final GenericStyle.Builder builder = GenericStyle.create();
        builder.name( styleElement.getAttribute( ELEMENT_NAME_ATTRIBUTE_NAME ) );

        final DomElement displayName = styleElement.getChild( DISPLAY_NAME_TAG_NAME );
        if ( displayName != null )
        {
            builder.displayName( displayName.getValue() );
            builder.displayNameI18nKey( displayName.getAttribute( I18N_ATTRIBUTE_NAME ) );
        }

        return builder.build();
    }
}
