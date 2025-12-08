package com.enonic.xp.core.impl.schema;

import java.net.URL;
import java.util.List;

public interface JsonSchemaService
{
    boolean registerSchema( String definition );

    boolean loadSchemas( List<URL> schemaURLs );

    void validate( String schemaId, String yml );

    void refreshSchemaRegistry();
}
