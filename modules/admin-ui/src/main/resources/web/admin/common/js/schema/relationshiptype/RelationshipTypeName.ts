module api.schema.relationshiptype {

    import ApplicationKey = api.application.ApplicationKey;

    export class RelationshipTypeName extends api.application.ApplicationBasedName {

        static REFERENCE = new RelationshipTypeName("reference");

        constructor(name:string) {
            api.util.assertNotNull(name, "RelationshipType name can't be null");
            var parts = name.split(api.application.ApplicationBasedName.SEPARATOR);
            super(ApplicationKey.fromString(parts[0]), parts[1]);
        }

    }
}