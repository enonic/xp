module api.content.site.template {

    export interface SiteTemplateJson extends SiteTemplateSummaryJson {

        pageTemplates:api.content.page.PageTemplateJson[];
    }
}