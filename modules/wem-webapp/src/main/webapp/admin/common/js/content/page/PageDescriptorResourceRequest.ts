module api_content_page {

    export class PageDescriptorResourceRequest<JSON> extends api_rest.ResourceRequest<JSON>{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "descriptor");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }

        fromJsonToPageDescriptor(json:api_content_page_json.PageDescriptorJson):api_content_page.PageDescriptor {
            return new api_content_page.PageDescriptorBuilder().fromJson(json).build();
        }
    }
}