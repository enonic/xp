package com.enonic.xp.repo.impl.node.json;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.io.ByteSource;

import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.util.BinaryReference;

public final class ImmutableVersionData
{
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHelper.create();

    static
    {
        OBJECT_MAPPER.addMixIn( ImmutableNodeVersion.class, ImmutableNodeVersionMixin.class );
        OBJECT_MAPPER.addMixIn( AttachedBinary.class, AttachedBinaryMixin.class );
    }

    private ImmutableVersionData()
    {
    }

    public static ImmutableNodeVersion deserialize( final ByteSource bytes )
        throws IOException
    {
        try (var is = bytes.openBufferedStream())
        {
            return OBJECT_MAPPER.readValue( is, ImmutableNodeVersion.class );
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private abstract static class ImmutableNodeVersionMixin
    {
        @JsonCreator
        ImmutableNodeVersionMixin( @JsonProperty("id") @JsonDeserialize(using = NodeIdDeserializer.class) final NodeId id,
                                          @JsonProperty("nodeType") @JsonDeserialize(using = NodeTypeDeserializer.class) final NodeType nodeType,
                                          @JsonProperty("data") @JsonDeserialize(contentUsing = ImmutablePropertyArrayDeserializer.class) final List<ImmutableProperty> data,
                                          @JsonProperty("childOrder") @JsonDeserialize(using = ChildOrderDeserializer.class) final ChildOrder childOrder,
                                          @JsonProperty("manualOrderValue") final Long manualOrderValue,
                                          @JsonProperty("attachedBinaries") @JsonDeserialize(using = AttachedBinariesDeserializer.class) final AttachedBinaries attachedBinaries )
        {
        }
    }

    private static class ImmutablePropertyArrayInner
    {
        @JsonProperty("name")
        String name;

        @JsonProperty("type")
        @JsonDeserialize(using = ValueTypeDeserializer.class)
        ValueType type;

        @JsonProperty("values")
        @JsonDeserialize(contentUsing = ValueInnerDeserializer.class)
        List<ValueInner> values;
    }

    private static class AttachedBinaryMixin
    {
        AttachedBinaryMixin( @JsonProperty("binaryReference")
                                    @JsonDeserialize(using = BinaryReferenceDeserializer.class)
                                    final BinaryReference binaryReference,
                                    @JsonProperty("blobKey")
                                    final String blobKey )
        {
        }
    }

    private static class ValueInner
    {
        @JsonProperty("v")
        Object v;

        @JsonProperty("set")
        List<ImmutablePropertyArrayInner> set;
    }

    private static class ValueInnerDeserializer
        extends JsonDeserializer<ValueInner>
    {
        @Override
        public ValueInner deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return jsonParser.readValueAs( ValueInner.class );
        }
    }

    private static class ImmutablePropertyArrayDeserializer
        extends JsonDeserializer<ImmutableProperty>
    {

        @Override
        public ImmutableProperty deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return ImmutableVersionData.convert( jsonParser.readValueAs( ImmutablePropertyArrayInner.class ) ) ;
        }
    }

    private static ImmutableProperty convert( final ImmutablePropertyArrayInner internal )
    {
        if ( internal.values.isEmpty() )
        {
            return ImmutableProperty.ofNoValue( internal.name, internal.type );
        }
        else if ( internal.type.equals( ValueTypes.PROPERTY_SET ) )
        {
            return ImmutableProperty.ofValueSet( internal.name, internal.values.stream()
                .map( value -> value.set == null
                    ? ImmutableProperty.nullValueSet()
                    : ImmutableProperty.toValueSet(
                        value.set.stream().map( ImmutableVersionData::convert ).collect( Collectors.toUnmodifiableList() ) ) )
                .collect( Collectors.toUnmodifiableList() ) );
        }
        else
        {
            return ImmutableProperty.ofValue( internal.name, internal.values.stream()
                .map( valueInner -> internal.type.fromJsonValue( valueInner.v ) )
                .collect( Collectors.toUnmodifiableList() ) );
        }
    }

    private static class NodeIdDeserializer
        extends JsonDeserializer<NodeId>
    {
        @Override
        public NodeId deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return NodeId.from( jsonParser.getText() );
        }
    }

    private static class NodeTypeDeserializer
        extends JsonDeserializer<NodeType>
    {

        @Override
        public NodeType deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return NodeType.from( jsonParser.getText() );
        }
    }

    private static class ChildOrderDeserializer
        extends JsonDeserializer<ChildOrder>
    {
        @Override
        public ChildOrder deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return ChildOrder.from( jsonParser.getText() );
        }
    }

    private static class ValueTypeDeserializer
        extends JsonDeserializer<ValueType>
    {
        @Override
        public ValueType deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return ValueTypes.getByName( jsonParser.getText() );
        }
    }

    private static class BinaryReferenceDeserializer
        extends JsonDeserializer<BinaryReference>
    {
        @Override
        public BinaryReference deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            return BinaryReference.from( jsonParser.getText() );
        }
    }

    private static class AttachedBinariesDeserializer
        extends JsonDeserializer<AttachedBinaries>
    {

        static final TypeReference<List<AttachedBinary>> VALUE_TYPE_REF = new TypeReference<>()
        {
        };

        @Override
        public AttachedBinaries deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
            throws IOException
        {
            final AttachedBinaries.Builder builder = AttachedBinaries.create();
            final List<AttachedBinary> list = jsonParser.readValueAs( VALUE_TYPE_REF );
            for ( final AttachedBinary entry : list )
            {
                builder.add( entry );
            }

            return builder.build();
        }
    }
}
