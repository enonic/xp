module api_schema_content {

    export class UpdateContentTypeRequest extends ContentTypeResourceRequest<any> {

        private contentTypeToUpdate:ContentTypeName;

        private name:ContentTypeName;

        private config:string;

        private iconReference:string;

        constructor(contentTypeToUpdate:ContentTypeName, name:ContentTypeName, config:string, iconReference:string) {
            super();
            super.setMethod('POST');
            this.contentTypeToUpdate = contentTypeToUpdate;
            this.name = name;
            this.config = config;
            this.iconReference = iconReference;
        }

        getParams():Object {
            return {
                contentTypeToUpdate: this.contentTypeToUpdate.toString(),
                name: this.name.toString(),
                config: this.config,
                iconReference: this.iconReference
            }
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}