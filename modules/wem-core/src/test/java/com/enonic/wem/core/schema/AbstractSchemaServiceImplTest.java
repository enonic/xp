package com.enonic.wem.core.schema;

import org.junit.Before;
import org.mockito.Mockito;

import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

public abstract class AbstractSchemaServiceImplTest
{
    protected SchemaServiceImpl schemaService;

    protected MixinService mixinService;

    protected ContentTypeService contentTypeService;

    protected RelationshipTypeService relationshipTypeService;

    @Before
    public void setUp()
    {
        this.schemaService = new SchemaServiceImpl();
        this.mixinService = this.schemaService.mixinService = Mockito.mock( MixinService.class );
        this.relationshipTypeService = this.schemaService.relationshipTypeService = Mockito.mock( RelationshipTypeService.class );
        this.contentTypeService = this.schemaService.contentTypeService = Mockito.mock( ContentTypeService.class );
    }
}
