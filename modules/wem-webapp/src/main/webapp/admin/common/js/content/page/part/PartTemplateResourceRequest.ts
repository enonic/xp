module api_content_page_part {

    export class PartTemplateResourceRequest extends api_rest.ResourceRequest<PartTemplate> {

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "part", "template");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }
    }
}