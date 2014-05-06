module api.content.page {

    export class PageDescriptorResourceRequest<JSON> extends api.rest.ResourceRequest<JSON>{

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