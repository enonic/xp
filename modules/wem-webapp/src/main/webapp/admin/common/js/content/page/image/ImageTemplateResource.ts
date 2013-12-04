module api_content_page_image {

    export class ImageTemplateResource<T> extends api_rest.ResourceRequest<T>{

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent( super.getRestPath(), "content", "page", "image", "template" );
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}
