package com.enonic.xp.lib.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateOrUpdateApplicationDescriptorParams;
import com.enonic.xp.app.EditableApplicationDescriptor;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.lib.app.mapper.ApplicationDescriptorMapper;
import com.enonic.xp.lib.app.mapper.IconByteSource;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNull;

/**
 * Creates or updates the application descriptor through an editor function: the function gets the descriptor as
 * {@code getDescriptor} returns it, changes what it wants and returns it. A key left out of the returned object keeps its value,
 * a key set to {@code null} clears it. The icon is kept when returned as received, removed when {@code null} and replaced when
 * given as {@code {data, mimeType}}.
 */
public final class CreateOrUpdateApplicationDescriptorHandler
    implements ScriptBean
{
    private String key;

    private ScriptValue editor;

    private Supplier<ApplicationDescriptorService> applicationDescriptorServiceSupplier;

    public ApplicationDescriptorMapper execute()
    {
        final CreateOrUpdateApplicationDescriptorParams params = CreateOrUpdateApplicationDescriptorParams.create()
            .key( ApplicationKey.from( key ) )
            .editor( this::edit )
            .build();

        return new ApplicationDescriptorMapper( applicationDescriptorServiceSupplier.get().createOrUpdate( params ) );
    }

    private void edit( final EditableApplicationDescriptor target )
    {
        if ( this.editor == null )
        {
            return;
        }
        final ScriptValue value = this.editor.call( new ApplicationDescriptorMapper( target.source ) );
        if ( value != null )
        {
            applyEdit( target, value.getMap() );
        }
    }

    private static void applyEdit( final EditableApplicationDescriptor target, final Map<String, ?> map )
    {
        edit( map, "title", val -> target.title = val.orElse( null ) );
        edit( map, "titleI18nKey", val -> target.titleI18nKey = val.orElse( null ) );
        edit( map, "description", val -> target.description = val.orElse( null ) );
        edit( map, "descriptionI18nKey", val -> target.descriptionI18nKey = val.orElse( null ) );
        edit( map, "vendorName", val -> target.vendorName = val.orElse( null ) );
        edit( map, "vendorUrl", val -> target.vendorUrl = val.orElse( null ) );
        edit( map, "url", val -> target.url = val.orElse( null ) );

        if ( map.containsKey( "config" ) )
        {
            target.schemaConfig = toConfig( map.get( "config" ) );
        }
        if ( map.containsKey( "icon" ) )
        {
            editIcon( target, map.get( "icon" ) );
        }
    }

    private static void edit( final Map<String, ?> map, final String key, final Consumer<Optional<String>> fieldEditor )
    {
        if ( map.containsKey( key ) )
        {
            fieldEditor.accept(
                Optional.ofNullable( map.get( key ) ).map( v -> requireNonNull( Converters.convert( v, String.class ), "cannot convert" ) ) );
        }
    }

    private static GenericValue toConfig( final Object raw )
    {
        if ( raw == null )
        {
            return GenericValue.newObject().build();
        }
        if ( raw instanceof Map<?, ?> map )
        {
            return GenericValue.fromRawJava( normalize( map ) );
        }
        throw new IllegalArgumentException( "config must be an object" );
    }

    /**
     * A JS object as config: keys with a {@code null} value are left out (as JSON would), numbers without a fraction are integers
     * (a JS number is a double, but {@code 42} written to the descriptor must stay {@code 42}).
     */
    private static Map<String, Object> normalize( final Map<?, ?> map )
    {
        final Map<String, Object> result = new LinkedHashMap<>();
        map.forEach( ( key, value ) -> {
            if ( value != null )
            {
                result.put( key.toString(), normalizeValue( value ) );
            }
        } );
        return result;
    }

    private static Object normalizeValue( final Object value )
    {
        return switch ( value )
        {
            case Map<?, ?> map -> normalize( map );
            case List<?> list ->
            {
                final List<Object> result = new ArrayList<>();
                list.forEach( item -> {
                    if ( item != null )
                    {
                        result.add( normalizeValue( item ) );
                    }
                } );
                yield result;
            }
            case Double d when d == Math.rint( d ) && !d.isInfinite() && Math.abs( d ) < Long.MAX_VALUE -> d.longValue();
            case Float f when f == Math.rint( f ) && !f.isInfinite() -> f.longValue();
            default -> value;
        };
    }

    private static void editIcon( final EditableApplicationDescriptor target, final Object raw )
    {
        if ( raw == null )
        {
            target.icon = null;
            return;
        }
        if ( !( raw instanceof Map<?, ?> map ) )
        {
            throw new IllegalArgumentException( "icon must be an object with data and mimeType, or null" );
        }

        final Object data = map.get( "data" );
        final String mimeType = Converters.convert( map.get( "mimeType" ), String.class );

        // the icon handed to the editor, returned as it was: nothing to change
        if ( data instanceof IconByteSource source && source.icon() == target.source.getIcon() &&
            Objects.equals( mimeType, source.icon().getMimeType() ) )
        {
            return;
        }

        if ( !( data instanceof ByteSource byteSource ) )
        {
            throw new IllegalArgumentException( "icon data must be a byte source" );
        }
        requireNonNull( mimeType, "icon mimeType is required" );

        try
        {
            target.icon = Icon.from( byteSource.read(), mimeType, Instant.now() );
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationDescriptorServiceSupplier = context.getService( ApplicationDescriptorService.class );
    }
}