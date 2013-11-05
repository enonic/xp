module api_schema {

    export class DeleteSchemaRequest extends SchemaResourceRequest {

        private qualifiedNames:string[] = [];

        private schemaKind:api_schema.SchemaKind;

        constructor(qualifiedNames?:string[]) {
            super();
            super.setMethod("POST");
            if (qualifiedNames) {
                this.setQualifiedNames(qualifiedNames);
            }
        }

        setQualifiedNames(qualifiedNames:string[]):DeleteSchemaRequest {
            this.qualifiedNames = qualifiedNames;
            return this;
        }

        addQualifiedName(qualifiedName:string):DeleteSchemaRequest {
            this.qualifiedNames.push(qualifiedName);
            return this;
        }

        setType(value:api_schema.SchemaKind):DeleteSchemaRequest {
            this.schemaKind = value;
            return this;
        }

        getParams():Object {
            return {
                qualifiedNames: this.qualifiedNames
            };
        }

        getRequestPath():api_rest.Path {

            var pathElement:string = null;

            if( this.schemaKind == api_schema.SchemaKind.CONTENT_TYPE ) {
                pathElement = "content";
            }
            else if( this.schemaKind == api_schema.SchemaKind.RELATIONSHIP_TYPE ) {
                pathElement = "relationship";
            }
            else if( this.schemaKind == api_schema.SchemaKind.MIXIN ) {
                pathElement = "mixin";
            }
            else {
                throw new Error("Unknown Schema kind: " + this.schemaKind.toString() )
            }

            return api_rest.Path.fromParent(super.getResourcePath(), pathElement, "delete");
        }
    }
}