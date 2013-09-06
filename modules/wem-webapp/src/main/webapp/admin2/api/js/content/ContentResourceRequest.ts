module api_content {

    export class ContentResourceRequest extends api_rest.ResourceRequest{

        private resourceUrl:string;

        constructor() {
            super();
            this.resourceUrl = super.getRestUrl() + "/content";
        }

        getResourceUrl():string {
            return this.resourceUrl;
        }
    }
}