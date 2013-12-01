module api_schema_content {

    export class DeleteContentTypeRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeJson> {

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

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "delete");
        }

    }

}