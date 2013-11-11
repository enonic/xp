module api_schema_content {

    export class UpdateContentTypeRequest extends ContentTypeResourceRequest<any> {

        private name:string;

        private contentType:string;

        private iconReference:string;

        constructor(name:string, contentType:string, iconReference:string) {
            super();
            super.setMethod('POST');
            this.name = name;
            this.contentType = contentType;
            this.iconReference = iconReference;
        }

        getParams():Object {
            return {
                name: this.name,
                contentType: this.contentType,
                iconReference: this.iconReference
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}