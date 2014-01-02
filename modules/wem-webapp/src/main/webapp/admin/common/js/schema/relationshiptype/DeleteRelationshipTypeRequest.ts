module api.schema.relationshiptype {

    export class DeleteRelationshipTypeRequest extends RelationshipTypeResourceRequest<api.schema.SchemaDeleteJson> {

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

        sendAndParse(): JQueryPromise<api.schema.SchemaDeleteResult> {
            var deferred = jQuery.Deferred<api.schema.SchemaDeleteResult>();

            this.send().done((response:api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {
                deferred.resolve(this.fromJsonToDeleteResult(response.getResult()));
            }).fail((response:api.rest.RequestError) => {
                deferred.reject(null);
            });

            return deferred;
        }

        fromJsonToDeleteResult(json:api.schema.SchemaDeleteJson): api.schema.SchemaDeleteResult {
            return new api.schema.SchemaDeleteResult(json);
        }
    }
}