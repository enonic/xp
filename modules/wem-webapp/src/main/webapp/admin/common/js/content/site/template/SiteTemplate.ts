module api.content.site.template {

    export class SiteTemplate extends SiteTemplateSummary {

        private pageTemplates:api.content.page.json.PageTemplateJson[];

        constructor(json: api.content.site.template.json.SiteTemplateJson) {
            super(json);

            this.pageTemplates = json.pageTemplates;
        }

        getDefaultPageTemplate( contentType:api.schema.content.ContentTypeName ):api.content.page.PageTemplate {
            this.pageTemplates.forEach((pageTemplateJson: api.content.page.json.PageTemplateJson) => {
                var pageTemplate = this.fromJsonToPageTemplate(pageTemplateJson);

                if (pageTemplate.isCanRender(contentType)) {
                    return pageTemplate;
                }
            });
            return null;
        }

        private fromJsonToPageTemplate(json:api.content.page.json.PageTemplateJson):api.content.page.PageTemplate {
            return new api.content.page.PageTemplateBuilder().fromJson(json).build();
        }
    }
}