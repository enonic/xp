module api.schema.metadata {

    import ModuleKey = api.module.ModuleKey;

    export class MetadataSchemaName extends api.schema.ModuleBasedName {

        constructor(name: string) {
            var parts = name.split(api.schema.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }
    }
}