package com.enonic.xp.resource;

public interface DynamicSchemaService
{
    Resource create( CreateDynamicSchemaParams params );

    Resource get( GetDynamicSchemaParams params );

    boolean delete( DeleteDynamicSchemaParams params );

}
