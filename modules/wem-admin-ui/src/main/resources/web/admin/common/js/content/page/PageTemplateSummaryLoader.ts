module api.content.page {

    export class PageTemplateSummaryLoader extends api.util.loader.BaseLoader<PageTemplateSummaryListJson, PageTemplateSummary> {

        constructor(request: PageTemplateResourceRequest<PageTemplateSummaryListJson, PageTemplateSummary[]>) {
            super(request);
        }

        filterFn(template:PageTemplateSummary) {
            return template.getDisplayName().toString().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

    }
}
