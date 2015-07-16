module api.schema.relationshiptype {

    import ApplicationKey = api.module.ApplicationKey;

    export class RelationshipTypeName extends api.module.ModuleBasedName {

        static REFERENCE = new RelationshipTypeName("reference");

        constructor(name:string) {
            api.util.assertNotNull(name, "RelationshipType name can't be null");
            var parts = name.split(api.module.ModuleBasedName.SEPARATOR);
            super(ApplicationKey.fromString(parts[0]), parts[1]);
        }

    }
}