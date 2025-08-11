package com.enonic.xp.lib.schema;

public interface JsonSchemaService
{
    String registerInputTypeSchema( final String jsonSchemaDefinition );

    boolean isContentTypeValid( final String contentTypeAsYml );

    boolean isSchemaValid( final String schemaId, final String yml );
}
