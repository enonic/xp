module api.schema {

    export class SchemaKind implements api.Equitable {

        public static CONTENT_TYPE = new SchemaKind("ContentType");

        private static CONTENT_TYPE_SUMMARY = new SchemaKind("ContentTypeSummary");

        public static RELATIONSHIP_TYPE = new SchemaKind("RelationshipType");

        public static MIXIN = new SchemaKind("Mixin");

        public static METADATA_SCHEMA = new SchemaKind("MetadataSchema");

        private name: string;

        static fromString(str: string): SchemaKind {

            if (SchemaKind.CONTENT_TYPE.nameEquals(str) || SchemaKind.CONTENT_TYPE_SUMMARY.nameEquals(str)) {
                return SchemaKind.CONTENT_TYPE;
            }
            else if (SchemaKind.RELATIONSHIP_TYPE.nameEquals(str)) {
                return SchemaKind.RELATIONSHIP_TYPE;
            }
            else if (SchemaKind.MIXIN.nameEquals(str)) {
                return SchemaKind.MIXIN;
            }
            else if (SchemaKind.METADATA_SCHEMA.nameEquals(str)) {
                return SchemaKind.METADATA_SCHEMA;
            }
            else {
                throw new Error("Unknown SchemaKind: " + str);
            }
        }

        constructor(name: string) {
            this.name = name;
        }

        nameEquals(str: string) {
            return str == this.name;
        }

        toString(): string {
            return this.name;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SchemaKind)) {
                return false;
            }

            var other = <SchemaKind>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            return true;
        }
    }

}