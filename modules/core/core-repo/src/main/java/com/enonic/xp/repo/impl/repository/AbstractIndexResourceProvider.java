package com.enonic.xp.repo.impl.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Resources;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.util.JsonHelper;

abstract class AbstractIndexResourceProvider
    implements IndexResourceProvider
{
    private final static String BASE_FOLDER = "/com/enonic/xp/repo/impl/repository/index";

    JsonNode getDefaultMapping( final IndexType indexType, final IndexResourceType type )
    {
        String fileName = BASE_FOLDER + "/" + type.getName() + "/" + "default" + "/" + indexType.getName() + "-" + type.getName() + ".json";

        try
        {
            return JsonHelper.from( Resources.getResource( IndexResourceClasspathProvider.class, fileName ) );
        }
        catch ( Exception e )
        {
            throw new IndexException( "[" + type + "] default from file: " + fileName + " not found", e );
        }
    }


}
