module api.schema.content {

    export class DeleteContentTypeRequest extends ContentTypeResourceRequest<api.schema.SchemaDeleteJson, api.schema.SchemaDeleteResult> {


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

        sendAndParse(): wemQ.Promise<api.schema.SchemaDeleteResult> {

            return this.send().then((response:api.rest.JsonResponse<api.schema.SchemaDeleteJson>) => {

                // TODO: Invalidate ContentTypeCache

                return this.fromJsonToDeleteResult(response.getResult());
            });
        }

        fromJsonToDeleteResult(json:api.schema.SchemaDeleteJson): api.schema.SchemaDeleteResult {
            return new api.schema.SchemaDeleteResult(json);
        }

    }

}