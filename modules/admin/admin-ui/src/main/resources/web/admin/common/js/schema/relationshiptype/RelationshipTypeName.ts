module api.schema.relationshiptype {

    import ApplicationKey = api.application.ApplicationKey;
    import ApplicationBasedName = api.application.ApplicationBasedName;

    export class RelationshipTypeName extends api.application.ApplicationBasedName {

        static REFERENCE: RelationshipTypeName = new RelationshipTypeName(
            `${ApplicationKey.SYSTEM}${ApplicationBasedName.SEPARATOR}reference`
        );

        constructor(name: string) {
            api.util.assertNotNull(name, "RelationshipType name can't be null");
            let parts = name.split(api.application.ApplicationBasedName.SEPARATOR);
            super(ApplicationKey.fromString(parts[0]), parts[1]);
        }

    }
}