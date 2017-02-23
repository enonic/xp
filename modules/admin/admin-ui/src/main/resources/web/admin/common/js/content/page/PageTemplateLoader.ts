module api.content.page {

    import ContentJson = api.content.json.ContentJson;
    import ListContentResult = api.content.resource.result.ListContentResult;

    export class PageTemplateLoader extends api.util.loader.BaseLoader<ListContentResult<ContentJson>, PageTemplate> {

        constructor(request: PageTemplateResourceRequest<ListContentResult<ContentJson>, PageTemplate[]>) {
            super(request);

            this.setComparator(new api.content.page.PageTemplateByDisplayNameComparator());
        }

        filterFn(template: PageTemplate) {
            return template.getDisplayName().toString().indexOf(this.getSearchString().toLowerCase()) !== -1;
        }

    }
}
