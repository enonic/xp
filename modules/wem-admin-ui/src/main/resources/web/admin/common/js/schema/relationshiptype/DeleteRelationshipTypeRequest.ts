module api.schema.relationshiptype {

    export class DeleteRelationshipTypeRequest extends RelationshipTypeResourceRequest<api.schema.SchemaDeleteJson, api.schema.SchemaDeleteResult> {

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

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "delete");
        }

        sendAndParse(): Q.Promise<api.schema.SchemaDeleteResult> {

            return this.send().then((response:api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {
                return this.fromJsonToDeleteResult(response.getResult());
            });
        }

        fromJsonToDeleteResult(json:api.schema.SchemaDeleteJson): api.schema.SchemaDeleteResult {
            return new api.schema.SchemaDeleteResult(json);
        }
    }
}