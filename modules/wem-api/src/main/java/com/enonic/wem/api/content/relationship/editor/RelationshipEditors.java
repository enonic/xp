package com.enonic.wem.api.content.relationship.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public abstract class RelationshipEditors
{
    public static RelationshipEditor composite( final RelationshipEditor... editors )
    {
        return new CompositeRelationshipEditor( editors );
    }

    public static RelationshipEditor removeProperties( final String[] remove )
    {
        return new RemoveRelationshipPropertiesEditor( Lists.newArrayList( remove ) );
    }

    public static SetBuilder setProperties()
    {
        return new SetBuilder();
    }

    public static RelationshipEditor addProperties( final Map<String, String> properties )
    {
        return new AddRelationshipPropertiesEditor( properties );
    }

    public static CompositeBuilder newCompositeBuilder()
    {
        return new CompositeBuilder();
    }

    public static class CompositeBuilder
    {
        private List<RelationshipEditor> list = new ArrayList<>();

        public CompositeBuilder add( RelationshipEditor editor )
        {
            list.add( editor );
            return this;
        }

        public RelationshipEditor build()
        {
            return composite( list.toArray( new RelationshipEditor[list.size()] ) );
        }
    }

    public static class SetBuilder
    {
        private ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();

        public SetBuilder add( final String key, final String value )
        {
            properties.put( key, value );
            return this;
        }


        public SetRelationshipPropertiesEditor build()
        {
            return new SetRelationshipPropertiesEditor( properties.build() );
        }
    }
}
