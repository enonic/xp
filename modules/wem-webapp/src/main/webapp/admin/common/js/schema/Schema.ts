module api.schema {

    export class Schema extends api.item.BaseItem {

        private key:string;

        private name:string;

        private displayName:string;

        private iconUrl:string;

        private kind:SchemaKind;

        static fromExtModel(model:Ext_data_Model):Schema {
            var schema:api.schema.Schema;
            var schemaKind = SchemaKind.fromString((<any>model.raw).schemaKind);

            if( schemaKind == SchemaKind.CONTENT_TYPE) {
                schema = new api.schema.content.ContentTypeSummary(<api.schema.content.json.ContentTypeSummaryJson>model.raw);
            }
            else if( schemaKind == SchemaKind.RELATIONSHIP_TYPE) {
                schema = new api.schema.relationshiptype.RelationshipType(<api.schema.relationshiptype.json.RelationshipTypeJson>model.raw);
            }
            else if( schemaKind == SchemaKind.MIXIN) {
                schema = new api.schema.mixin.Mixin(<api.schema.mixin.json.MixinJson>model.raw);
            }
            else {
                console.log("Unknown Ext.data_Model for Schema: ", model );
                throw new Error("Unknown Schema kind: " + schemaKind );
            }
            return schema;
        }

        constructor(json:api.schema.SchemaJson) {
            super(json, "key");
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