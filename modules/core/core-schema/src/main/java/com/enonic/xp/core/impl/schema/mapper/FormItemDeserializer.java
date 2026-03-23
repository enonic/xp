package com.enonic.xp.core.impl.schema.mapper;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FieldSet;
import com.enonic.xp.form.FormFragment;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemSet;
import com.enonic.xp.form.FormOptionSet;
import com.enonic.xp.form.FormOptionSetOption;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.util.GenericValue;

public class FormItemDeserializer
    extends JsonDeserializer<FormItem>
{
    @Override
    public FormItem deserialize( final JsonParser jsonParser, final DeserializationContext ctxt )
        throws IOException
    {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        final JsonNode node = mapper.readTree( jsonParser );

        final String include = node.path( "include" ).asText( null );

        final ApplicationKey currentApplication =  resolveApplicationKey( ctxt );

        if ( include != null && node.size() == 1 )
        {
            final FormFragmentName fragmentName = FormFragmentNameResolver.resolve( include, currentApplication);
            return FormFragment.create().formFragment( fragmentName ).build();
        }

        final String type = node.get( "type" ).asText();

        return switch ( type )
        {
            case "FieldSet" -> mapper.treeToValue( node, FieldSet.class );
            case "ItemSet" -> mapper.treeToValue( node, FormItemSet.class );
            case "OptionSet" -> mapper.treeToValue( node, FormOptionSet.class );
            case "OptionSetOption" -> mapper.treeToValue( node, FormOptionSetOption.class );
            case "FormFragment" -> mapper.treeToValue( node, FormFragment.class );
            default ->
            {
                final InputYml inputYml = mapper.treeToValue( node, InputYml.class );

                if ( InputTypeName.CUSTOM_SELECTOR.toString().equals( inputYml.type ) )
                {
                    final Map<String, GenericValue> topLevelAttributes = inputYml.getTopLevelAttributes();
                    if ( topLevelAttributes.containsKey( "service" ) )
                    {
                        topLevelAttributes.put( "service", GenericValue.stringValue(
                            toServiceUrl( topLevelAttributes.get( "service" ).asString(), resolveApplicationKey( ctxt ) ) ) );
                    }
                }

                yield inputYml.convertToInput();
            }
        };
    }

    private ApplicationKey resolveApplicationKey( final DeserializationContext ctxt )
        throws JsonMappingException
    {
        return (ApplicationKey) ctxt.findInjectableValue( "currentApplication", null, null, null, null );
    }

    private static String toServiceUrl( final String serviceName, final ApplicationKey currentApplication )
    {
        if ( serviceName.contains( ":" ) )
        {
            if ( serviceName.startsWith( "http" ) )
            {
                // points to an external location
                return serviceName;
            }
            else
            {
                // points to another app
                return serviceName.replace( ":", "/" );
            }
        }

        if ( currentApplication == null )
        {
            throw new IllegalArgumentException( String.format( "Unable to resolve application for Service [%s]", serviceName ) );
        }

        return currentApplication + "/" + serviceName;
    }
}
