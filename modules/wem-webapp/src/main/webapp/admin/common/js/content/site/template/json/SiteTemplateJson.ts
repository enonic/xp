module api.content.site.template.json {

    export interface SiteTemplateJson extends SiteTemplateSummaryJson {

        pageTemplates:api.content.page.json.PageTemplateJson[];
    }
}