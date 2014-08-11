module api.content.page {

    export class PageDescriptorResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE>{

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "descriptor");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPageDescriptor(json:api.content.page.PageDescriptorJson):api.content.page.PageDescriptor {
            return new api.content.page.PageDescriptorBuilder().fromJson(json).build();
        }
    }
}