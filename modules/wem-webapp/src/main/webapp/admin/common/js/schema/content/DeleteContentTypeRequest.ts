module api.schema.content {

    export class DeleteContentTypeRequest extends ContentTypeResourceRequest<api.schema.SchemaDeleteJson> {


        private names: string[] = [];

        constructor(names?:string[]) {
            super();
            super.setMethod("POST");
            if (names) {
                this.addNames(names);
            }
        }

        addNames(names:string[]):DeleteContentTypeRequest {
            this.names = names;
            return this;
        }

        addName(name:ContentTypeName):DeleteContentTypeRequest {
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

            var deferred = Q.defer<api.schema.SchemaDeleteResult>();

            this.send().done((response:api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {
                deferred.resolve(this.fromJsonToDeleteResult(response.getResult()));
            }).fail((response:api.rest.RequestError) => {
                deferred.reject(null);
            });

            return deferred.promise;
        }

        fromJsonToDeleteResult(json:api.schema.SchemaDeleteJson): api.schema.SchemaDeleteResult {
            return new api.schema.SchemaDeleteResult(json);
        }

    }

}