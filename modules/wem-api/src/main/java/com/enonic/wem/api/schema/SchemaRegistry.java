package com.enonic.wem.api.schema;

import com.enonic.wem.api.schema.metadata.Metadata;
import com.enonic.wem.api.schema.metadata.MetadataName;
import com.enonic.wem.api.schema.metadata.Metadatas;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;

public interface SchemaRegistry
{

    Schema getSchema( SchemaName schemaName );

    ContentType getContentType( ContentTypeName contentTypeName );

    Mixin getMixin( MixinName mixinName );

    RelationshipType getRelationshipType( RelationshipTypeName relationshipTypeName );

    Metadata getMetadata(MetadataName metadataName);

    Schemas getAllSchemas();

    ContentTypes getAllContentTypes();

    Mixins getAllMixins();

    RelationshipTypes getAllRelationshipTypes();

    Metadatas getAllMetadatas();

    Schemas getModuleSchemas( ModuleKey moduleKey );

}
