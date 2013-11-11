module api_schema_content {

    export class DeleteContentTypeRequest extends ContentTypeResourceRequest<api_schema_content_json.ContentTypeJson> {

        private qualifiedNames: string[] = [];

        constructor(qualifiedNames?:string[]) {
            super();
            super.setMethod("POST");
            if (qualifiedNames) {
                this.setQualifiedNames(qualifiedNames);
            }
        }

        setQualifiedNames(qualifiedNames:string[]):DeleteContentTypeRequest {
            this.qualifiedNames = qualifiedNames;
            return this;
        }

        addQualifiedName(qualifiedName:string):DeleteContentTypeRequest {
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