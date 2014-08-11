module api.content.page.image {

    export class ImageDescriptorResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "image", "descriptor");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToImageDescriptor(json: ImageDescriptorJson): ImageDescriptor {

            return new api.content.page.image.ImageDescriptorBuilder().fromJson(json).build();
        }
    }
}