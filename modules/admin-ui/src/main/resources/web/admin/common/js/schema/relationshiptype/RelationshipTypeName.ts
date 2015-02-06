module api.schema.relationshiptype {

    import ModuleKey = api.module.ModuleKey;

    export class RelationshipTypeName extends api.module.ModuleBasedName {

        static REFERENCE = new RelationshipTypeName("reference");

        constructor(name:string) {
            api.util.assertNotNull(name, "RelationshipType name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ModuleKey.fromString(parts[0]), parts[1]);
        }

    }
}