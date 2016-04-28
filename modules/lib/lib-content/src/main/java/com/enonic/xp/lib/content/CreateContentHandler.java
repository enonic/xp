package com.enonic.xp.lib.content;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.ContentMapper;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.ScriptValue;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public final class CreateContentHandler
    extends BaseContextHandler
{
    private final static Random RANDOM = new SecureRandom();

    private String name;

    private String parentPath;

    private String displayName;

    private boolean requireValid = true;

    private Map<String, Object> data;

    private Map<String, Object> x;

    private String contentType;

    private String language;

    private Supplier<String> idGenerator = () -> Long.toString( Math.abs( RANDOM.nextLong() ) );

    @Override
    protected Object doExecute()
    {
        if ( isBlank( this.name ) && isNotBlank( this.displayName ) && isNotBlank( this.parentPath ) )
        {
            this.name = generateUniqueContentName( ContentPath.from( this.parentPath ), this.displayName );
        }
        if ( isBlank( this.displayName ) && isNotBlank( this.name ) )
        {
            this.displayName = this.name;
        }

        final CreateContentParams params = createParams();
        final Content result = this.contentService.create( params );
        return new ContentMapper( result );
    }

    private CreateContentParams createParams()
    {
        final ContentTypeName contentTypeName = contentTypeName( this.contentType );

        return CreateContentParams.create().
            name( this.name ).
            parent( contentPath( this.parentPath ) ).
            displayName( this.displayName ).
            requireValid( this.requireValid ).
            type( contentTypeName ).
            contentData( createPropertyTree( data, contentTypeName ) ).
            extraDatas( createExtraDatas( x ) ).
            language( language != null ? Locale.forLanguageTag( language ) : null ).
            build();
    }

    private ContentPath contentPath( final String value )
    {
        return value != null ? ContentPath.from( value ) : null;
    }

    private ContentTypeName contentTypeName( final String value )
    {
        return value != null ? ContentTypeName.from( value ) : null;
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final ContentTypeName contentTypeName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), contentTypeName );
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final MixinName mixinName )
    {
        if ( value == null )
        {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), mixinName );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    private ExtraDatas createExtraDatas( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        for ( final String applicationPrefix : value.keySet() )
        {
            final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( applicationPrefix );
            final Object extradatasObject = value.get( applicationPrefix );
            if ( !( extradatasObject instanceof Map ) )
            {
                continue;
            }

            final Map<?, ?> extradatas = (Map<?, ?>) extradatasObject;
            for ( final Map.Entry<?, ?> entry : extradatas.entrySet() )
            {
                final MixinName mixinName = MixinName.from( applicationKey, entry.getKey().toString() );
                final ExtraData item = createExtraData( mixinName, entry.getValue() );
                if ( item != null )
                {
                    extradatasBuilder.add( item );
                }
            }
        }

        return extradatasBuilder.build();
    }

    private ExtraData createExtraData( final MixinName mixinName, final Object value )
    {
        if ( value instanceof Map )
        {
            final PropertyTree tree = createPropertyTree( (Map) value, mixinName );
            return tree != null ? new ExtraData( mixinName, tree ) : null;
        }

        return null;
    }

    private String generateUniqueContentName( final ContentPath parent, final String displayName )
    {
        final String baseName = NamePrettyfier.create( displayName );

        String name = baseName;
        while ( this.contentService.contentExists( ContentPath.from( parent, name ) ) )
        {
            final String randomId = this.idGenerator.get();
            name = NamePrettyfier.create( baseName + "-" + randomId );
        }

        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setRequireValid( final boolean requireValid )
    {
        this.requireValid = requireValid;
    }

    public void setData( final ScriptValue data )
    {
        this.data = data != null ? data.getMap() : null;
    }

    public void setX( final ScriptValue x )
    {
        this.x = x != null ? x.getMap() : null;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public void setLanguage( final String language )
    {
        this.language = language;
    }

    public void setIdGenerator( final Supplier<String> idGenerator )
    {
        if ( idGenerator != null )
        {
            this.idGenerator = idGenerator;
        }
    }
}
