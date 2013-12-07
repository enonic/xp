module api_content_page_image {

    export class ImageTemplateResource<T> extends api_rest.ResourceRequest<T> {

        private resourcePath: api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "image", "template");
        }

        getResourcePath(): api_rest.Path {
            return this.resourcePath;
        }

        fromJsonToImageTemplate(json: api_content_page_image_json.ImageTemplateJson): api_content_page_image.ImageTemplate {
            return new ImageTemplateBuilder().fromJson(json).build();
        }
    }
}
