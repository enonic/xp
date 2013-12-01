module api_schema_content {

    export class CreateContentTypeRequest extends ContentTypeResourceRequest<any> {

        private name:string;

        private config:string;

        private iconReference:string;

        constructor(name:string, contentType:string, iconReference:string) {
            super();
            super.setMethod('POST');
            this.name = name;
            this.config = contentType;
            this.iconReference = iconReference;
        }

        getParams():Object {
            return {
                name: this.name,
                config: this.config,
                iconReference: this.iconReference
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "create");
        }
    }

}