package com.enonic.wem.api.userstore.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class UserStoreConfig
    implements Iterable<UserStoreFieldConfig>
{
    private final Map<String, UserStoreFieldConfig> map;

    public UserStoreConfig()
    {
        this.map = Maps.newHashMap();
    }

    public List<UserStoreFieldConfig> getFields()
    {
        return ImmutableList.copyOf( this.map.values() );
    }

    public Map<String, UserStoreFieldConfig> getFieldMap()
    {
        return ImmutableMap.copyOf( this.map );
    }

    @Override
    public Iterator<UserStoreFieldConfig> iterator()
    {
        return getFields().iterator();
    }

    public UserStoreFieldConfig getField( final String name )
    {
        return this.map.get( name );
    }

    public void addField( final UserStoreFieldConfig field )
    {
        this.map.put( field.getName(), field );
    }

    public void removeField( final String fieldName )
    {
        this.map.remove( fieldName );
    }
}
