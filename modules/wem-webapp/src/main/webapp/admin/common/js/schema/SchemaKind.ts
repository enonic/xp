module api_schema {

    export class SchemaKind {

        public static CONTENT_TYPE = new SchemaKind("ContentType");

        private static CONTENT_TYPE_SUMMARY = new SchemaKind("ContentTypeSummary");

        public static RELATIONSHIP_TYPE = new SchemaKind("RelationshipType");

        public static MIXIN = new SchemaKind("Mixin");

        private name:string;

        static fromString(str:string):SchemaKind{

            if( SchemaKind.CONTENT_TYPE.nameEquals(str) || SchemaKind.CONTENT_TYPE_SUMMARY.nameEquals(str) ) {
                return SchemaKind.CONTENT_TYPE;
            }
            else if( SchemaKind.RELATIONSHIP_TYPE.nameEquals(str) ) {
                return SchemaKind.RELATIONSHIP_TYPE;
            }
            else if( SchemaKind.MIXIN.nameEquals(str) ) {
                return SchemaKind.MIXIN;
            }
            else {
                throw new Error("Unknown SchemaKind: " + str);
            }
        }

        constructor( name:string ){
            this.name = name;
        }

        nameEquals(str:string) {
            return str == this.name;
        }

        toString():string {
            return this.name;
        }

        equals(obj:any):boolean {
            return (obj instanceof SchemaKind) && (this.nameEquals((<SchemaKind> obj).name));
        }
    }

}