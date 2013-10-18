module api_schema {

    export class DeleteSchemaRequest extends api_rest.ResourceRequest {

        public static CONTENT_TYPE = "ContentType";
        public static RELATIONSHIP_TYPE = "RelationshipType";
        public static MIXIN = "Mixin";

        private qualifiedNames:string[] = [];
        private type:string = DeleteSchemaRequest.CONTENT_TYPE;

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

        setType(type:string):DeleteSchemaRequest {
            this.type = type;
            return this;
        }

        getParams():Object {
            return {
                qualifiedNames: this.qualifiedNames
            };
        }

        getRequestPath():api_rest.Path {
            var schemaType = undefined;
            switch ( this.type ) {
                case DeleteSchemaRequest.CONTENT_TYPE:
                    schemaType = "schema/content";
                    break;
                case DeleteSchemaRequest.RELATIONSHIP_TYPE:
                    schemaType = "schema/relationship";
                    break;
                case DeleteSchemaRequest.MIXIN:
                    schemaType = "schema/mixin";
                    break;
            }
            return api_rest.Path.fromParent(super.getRestPath(), schemaType, "delete");
        }
    }
}