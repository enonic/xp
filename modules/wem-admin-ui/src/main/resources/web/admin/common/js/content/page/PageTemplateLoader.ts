module api.content.page {

    import ContentJson = api.content.json.ContentJson;
    import ListContentResult = api.content.ListContentResult;

    export class PageTemplateLoader extends api.util.loader.BaseLoader<ListContentResult<ContentJson>, PageTemplate> {

        constructor(request: PageTemplateResourceRequest<ListContentResult<ContentJson>, PageTemplate[]>) {
            super(request);
        }

        filterFn(template: PageTemplate) {
            return template.getDisplayName().toString().indexOf(this.getSearchString().toLowerCase()) != -1;
        }

    }
}
