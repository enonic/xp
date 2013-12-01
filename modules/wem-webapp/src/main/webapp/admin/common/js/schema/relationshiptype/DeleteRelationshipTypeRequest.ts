module api_schema_relationshiptype {

    export class DeleteRelationshipTypeRequest extends RelationshipTypeResourceRequest<api_schema.SchemaDeleteJson> {

        private names: string[] = [];

        constructor(names?:string[]) {
            super();
            super.setMethod("POST");
            if (names) {
                this.setNames(names);
            }
        }

        setNames(names:string[]):DeleteRelationshipTypeRequest {
            this.names = names;
            return this;
        }

        addName(name:RelationshipTypeName):DeleteRelationshipTypeRequest {
            this.names.push(name.toString());
            return this;
        }

        getParams():Object {
            return {
                names: this.names
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}