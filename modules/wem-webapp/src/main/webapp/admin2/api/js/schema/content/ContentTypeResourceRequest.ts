module api_schema_content {

    export class ContentTypeResourceRequest extends api_rest.ResourceRequest{

        private resourceUrl:string;

        constructor() {
            super();
            this.resourceUrl = super.getRestUrl() + "/schema/content";
        }

        getResourceUrl():string {
            return this.resourceUrl;
        }
    }
}