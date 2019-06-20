package com.enonic.xp.lib.content;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public abstract class BaseContentHandler
    extends BaseContextHandler
{
    protected PropertyTree createPropertyTree( final Map<?, ?> value, final ContentTypeName contentTypeName )
    {
        if (value == null) {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), contentTypeName );
    }

    private PropertyTree createPropertyTree( final Map<?, ?> value, final XDataName xDataName, final ContentTypeName contentTypeName )
    {
        if (value == null) {
            return null;
        }

        return this.translateToPropertyTree( createJson( value ), xDataName, contentTypeName );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    protected ExtraDatas createExtraDatas( final Map<String, Object> value, final ContentTypeName contentTypeName )
    {
        if (value == null) {
            return null;
        }

        final ExtraDatas.Builder extradatasBuilder = ExtraDatas.create();
        for (final String applicationPrefix : value.keySet()) {
            final ApplicationKey applicationKey = ExtraData.fromApplicationPrefix( applicationPrefix );
            final Object extradatasObject = value.get( applicationPrefix );
            if (!(extradatasObject instanceof Map)) {
                continue;
            }

            final Map<?, ?> extradatas = (Map<?, ?>) extradatasObject;
            for (final Map.Entry<?, ?> entry : extradatas.entrySet()) {
                final XDataName xDataName = XDataName.from( applicationKey, entry.getKey().toString() );
                final ExtraData item = createExtraData( xDataName, contentTypeName, entry.getValue() );
                if (item != null) {
                    extradatasBuilder.add( item );
                }
            }
        }

        return extradatasBuilder.build();
    }

    private ExtraData createExtraData( final XDataName xDataName, final ContentTypeName contentTypeName, final Object value )
    {
        if (value instanceof Map) {
            final PropertyTree propertyTree = createPropertyTree( (Map) value, xDataName, contentTypeName );

            if (propertyTree != null) {
                return new ExtraData( xDataName, propertyTree );
            }
        }

        return null;
    }

    protected WorkflowInfo createWorkflowInfo( Map<String, Object> value )
    {
        if (value == null) {
            return null;
        }

        Object state = value.get( "state" );
        Object checks = value.get( "checks" );
        ImmutableMap.Builder<String, WorkflowCheckState> checkMapBuilder = ImmutableMap.builder();

        if (checks != null) {
            ((Map<String, String>) checks).entrySet().
                forEach( e -> checkMapBuilder.put(
                    e.getKey(),
                    WorkflowCheckState.valueOf( e.getValue() )
                    )
                );
        }

        return WorkflowInfo.create().
            state( state instanceof String ? (String) state : null ).
            checks( checkMapBuilder.build() ).
            build();
    }
}
