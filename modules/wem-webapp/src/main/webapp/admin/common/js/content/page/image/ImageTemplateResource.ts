module api.content.page.image {

    export class ImageTemplateResource<T> extends api.rest.ResourceRequest<T> {

        private resourcePath: api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "image", "template");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToImageTemplate(json: json.ImageTemplateJson): ImageTemplate {
            return new ImageTemplateBuilder().fromJson(json).build();
        }

        fromJsonToImageTemplateSummary(json: json.ImageTemplateSummaryJson): ImageTemplateSummary {
            return ImageTemplateSummaryBuilder.fromJson(json).build();
        }
    }
}
