module api.content.page.layout {

    export class LayoutDescriptorResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "layout", "descriptor");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToLayoutDescriptor(json: LayoutDescriptorJson): LayoutDescriptor {

            return new api.content.page.layout.LayoutDescriptorBuilder().fromJson(json).build();
        }
    }
}