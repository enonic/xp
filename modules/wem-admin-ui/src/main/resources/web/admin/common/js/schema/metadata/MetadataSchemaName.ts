module api.schema.metadata {

    import ModuleKey = api.module.ModuleKey;

    export class MetadataSchemaName extends api.module.ModuleBasedName {

        constructor(name: string) {
            api.util.assertNotNull(name, "Metadata schema name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }
    }
}