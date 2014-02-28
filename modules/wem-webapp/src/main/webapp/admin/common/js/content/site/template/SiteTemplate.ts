module api.content.site.template {

    export class SiteTemplate extends SiteTemplateSummary {

        private pageTemplates: api.content.page.PageTemplate[];

        constructor(json: api.content.site.template.json.SiteTemplateJson) {
            super(json);

            this.pageTemplates = [];
            json.pageTemplates.forEach((pageTemplateJson: api.content.page.json.PageTemplateJson)=> {
                this.pageTemplates.push(new api.content.page.PageTemplateBuilder().fromJson(pageTemplateJson).build());
            });
        }
    }
}