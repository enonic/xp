module api_schema {

    export class Schema extends api_item.BaseItem {

        private key:string;

        private name:string;

        private displayName:string;

        private iconUrl:string;

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
            this.key = json.key;
            this.name = json.name;
            this.displayName = json.displayName;
            this.iconUrl = json.iconUrl;
            this.kind = SchemaKind.fromString(json.schemaKind );
        }

        getKey():string {
            return this.key;
        }

        getName():string {
            return this.name;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getIconUrl():string{
            return this.iconUrl;
        }

        getSchemaKind():SchemaKind {
            return this.kind;
        }

    }
}