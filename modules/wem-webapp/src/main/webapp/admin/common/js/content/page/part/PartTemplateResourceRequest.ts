module api.content.page.part {

    export class PartTemplateResourceRequest<T> extends api.rest.ResourceRequest<T> {

        private resourcePath:api.rest.Path;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "part", "template");
        }

        getResourcePath():api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPartTemplate(json:api.content.page.part.json.PartTemplateJson): api.content.page.part.PartTemplate {
            return new PartTemplateBuilder().fromJson(json).build();
        }
    }
}