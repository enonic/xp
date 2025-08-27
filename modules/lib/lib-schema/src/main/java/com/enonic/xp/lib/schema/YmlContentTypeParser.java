package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataNames;

public final class YmlContentTypeParser
{
    private ContentType.Builder builder;

    private ApplicationKey currentApplication;

    private ApplicationRelativeResolver resolver;

    public YmlContentTypeParser builder( final ContentType.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    public YmlContentTypeParser currentApplication( final ApplicationKey currentApplication )
    {
        this.currentApplication = currentApplication;
        return this;
    }

    @SuppressWarnings("unchecked")
    public void parse( final Map<String, Object> contentTypeAsMap )
    {
        this.resolver = new ApplicationRelativeResolver( this.currentApplication );

        builder.name( "article" );

        parseDisplayName( contentTypeAsMap );
        parseDescription( contentTypeAsMap );

        builder.superType( resolver.toContentTypeName( getTrimmedValue( (String) contentTypeAsMap.get( "superType" ) ) ) );
        builder.setAbstract( Objects.requireNonNullElse( (Boolean) contentTypeAsMap.get( "isAbstract" ), false ) );
        builder.setFinal( Objects.requireNonNullElse( (Boolean) contentTypeAsMap.get( "isFinal" ), false ) );
        builder.allowChildContent( Objects.requireNonNullElse( (Boolean) contentTypeAsMap.get( "allowChildContent" ), true ) );

        final List<String> allowChildContentTypes = (List<String>) contentTypeAsMap.get( "allowChildContentType" );
        if ( allowChildContentTypes != null )
        {
            builder.allowChildContentType( allowChildContentTypes.stream()
                                               .filter( Objects::nonNull )
                                               .map( String::trim )
                                               .filter( s -> !s.isEmpty() )
                                               .collect( Collectors.toList() ) );
        }

        final List<Map<String, Object>> xDataElements = (List<Map<String, Object>>) contentTypeAsMap.get( "xData" );
        if ( xDataElements != null )
        {
            final List<XDataName> names = xDataElements.stream().map( xDataElement -> {
                final String xDataName = (String) xDataElement.get( "name" );
                return resolver.toXDataName( xDataName );
            } ).toList();
            builder.xData( XDataNames.from( names ) );
        }

        final YmlFormParser formParser = new YmlFormParser( currentApplication );
        builder.form( formParser.parse( (List<Map<String, Object>>) contentTypeAsMap.get( "form" ) ) );
    }

    @SuppressWarnings("unchecked")
    private void parseDisplayName( final Map<String, Object> contentTypeAsMap )
    {
        final Map<String, Object> displayName = (Map<String, Object>) contentTypeAsMap.get( "displayName" );

        if ( displayName != null )
        {
            builder.displayName( getTrimmedValue( (String) displayName.get( "text" ) ) );
            builder.displayNameI18nKey( (String) displayName.get( "i18n" ) );
            builder.displayNameExpression( getTrimmedValue( (String) displayName.get( "expression" ) ) );
        }

        final Map<String, Object> displayNameLabel = (Map<String, Object>) contentTypeAsMap.get( "label" );

        if ( displayNameLabel != null )
        {
            builder.displayNameLabel( getTrimmedValue( (String) displayNameLabel.get( "text" ) ) );
            builder.displayNameLabelI18nKey( (String) displayNameLabel.get( "i18n" ) );
        }
    }

    @SuppressWarnings("unchecked")
    private void parseDescription( final Map<String, Object> contentTypeAsMap )
    {
        final Map<String, Object> description = (Map<String, Object>) contentTypeAsMap.get( "description" );

        if ( description != null )
        {
            builder.description( getTrimmedValue( (String) description.get( "text" ) ) );
            builder.descriptionI18nKey( (String) description.get( "i18n" ) );
        }
    }

    private String getTrimmedValue( final String value )
    {
        return value != null ? value.trim() : null;
    }

}
