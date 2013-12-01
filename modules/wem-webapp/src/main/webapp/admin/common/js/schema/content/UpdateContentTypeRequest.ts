module api_schema_content {

    export class UpdateContentTypeRequest extends ContentTypeResourceRequest<any> {

        private name:string;

        private config:string;

        private iconReference:string;

        constructor(name:string, config:string, iconReference:string) {
            super();
            super.setMethod('POST');
            this.name = name;
            this.config = config;
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
            return api_rest.Path.fromParent(super.getResourcePath(), "update");
        }
    }
}