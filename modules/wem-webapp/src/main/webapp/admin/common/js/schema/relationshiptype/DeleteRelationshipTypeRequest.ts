module api_schema_relationshiptype {

    export class DeleteRelationshipTypeRequest extends RelationshipTypeResourceRequest<api_schema.SchemaDeleteJson> {

        private qualifiedNames: string[] = [];

        constructor(qualifiedNames?:string[]) {
            super();
            super.setMethod("POST");
            if (qualifiedNames) {
                this.setQualifiedNames(qualifiedNames);
            }
        }

        setQualifiedNames(qualifiedNames:string[]):DeleteRelationshipTypeRequest {
            this.qualifiedNames = qualifiedNames;
            return this;
        }

        addQualifiedName(qualifiedName:string):DeleteRelationshipTypeRequest {
            this.qualifiedNames.push(qualifiedName);
            return this;
        }

        getParams():Object {
            return {
                qualifiedNames: this.qualifiedNames
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }
    }
}