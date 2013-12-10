module api_content_page_part {

    export class PartTemplateResourceRequest<T> extends api_rest.ResourceRequest<T> {

        private resourcePath:api_rest.Path;

        constructor() {
            super();
            this.resourcePath = api_rest.Path.fromParent(super.getRestPath(), "content", "page", "part", "template");
        }

        getResourcePath():api_rest.Path {
            return this.resourcePath;
        }

        fromJsonToPartTemplate(json:api_content_page_part_json.PartTemplateJson): api_content_page_part.PartTemplate {
            return new PartTemplateBuilder().fromJson(json).build();
        }
    }
}