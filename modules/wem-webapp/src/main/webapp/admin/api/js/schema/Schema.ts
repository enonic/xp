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
    }

    export class Schema extends api_item.BaseItem {

        private name:string;

        private displayName:string;

        private icon:string;

        private kind:SchemaKind;

        static fromExtModel(model:Ext_data_Model):Schema {
            var schema:api_schema.Schema;
            var schemaKind = SchemaKind.fromString((<any>model.raw).schemaKind);

            if( schemaKind == SchemaKind.CONTENT_TYPE) {
                schema = new api_schema_content.ContentTypeSummary(<api_schema_content_json.ContentTypeSummaryJson>model.raw);
            }
            else if( schemaKind == SchemaKind.RELATIONSHIP_TYPE) {
                schema = new api_schema_relationshiptype.RelationshipType(<api_schema_relationshiptype_json.RelationshipTypeJson>model.raw);
            }
            else if( schemaKind == SchemaKind.MIXIN) {
                schema = new api_schema_mixin.Mixin(<api_schema_mixin_json.MixinJson>model.raw);
            }
            else {
                console.log("Unknown Ext_data_Model for Schema: ", model );
                throw new Error("Unknown Schema kind: " + schemaKind );
            }
            return schema;
        }

        constructor(json:api_schema.SchemaJson) {
            super(json);
            this.name = json.name;
            this.displayName = json.displayName;
            this.icon = json.iconUrl;
            this.kind = SchemaKind.fromString(json.schemaKind );
        }

        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getIcon():string {
            return this.icon;
        }

        getSchemaKind():SchemaKind {
            return this.kind;
        }

    }
}