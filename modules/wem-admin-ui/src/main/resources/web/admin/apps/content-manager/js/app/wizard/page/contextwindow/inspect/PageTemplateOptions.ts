module app.wizard.page.contextwindow.inspect {

    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
    import PageTemplate = api.content.page.PageTemplate;
    import PageTemplateLoader = api.content.page.PageTemplateLoader;
    import Option = api.ui.selector.Option;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PageTemplateOptions {

        private siteId: ContentId;

        private contentType: ContentTypeName;

        private static defaultPageTemplateOption: Option<PageTemplateOption> = {value: "__auto__", displayValue: new PageTemplateOption(null)};

        constructor(siteId: ContentId, contentType: ContentTypeName) {
            this.siteId = siteId;
            this.contentType = contentType;
        }

        static getDefault(): Option<PageTemplateOption> {
            return this.defaultPageTemplateOption;
        }

        getOptions(): wemQ.Promise<Option<PageTemplateOption>[]> {

            var deferred = wemQ.defer<Option<PageTemplateOption>[]>();

            var options: Option<PageTemplateOption>[] = [];
            options.push(PageTemplateOptions.defaultPageTemplateOption);

            var loader = new PageTemplateLoader(new GetPageTemplatesByCanRenderRequest(this.siteId,
                this.contentType));

            loader.onLoadedData((event: LoadedDataEvent<PageTemplate>) => {

                var pageTemplates: PageTemplate[] = event.getData();

                pageTemplates.forEach((pageTemplate: PageTemplate, index: number) => {

                    var indices: string[] = [];
                    indices.push(pageTemplate.getName().toString());
                    indices.push(pageTemplate.getDisplayName());
                    indices.push(pageTemplate.getController().toString());

                    var option = {
                        value: pageTemplate.getId().toString(),
                        displayValue: new PageTemplateOption(pageTemplate),
                        indices: indices
                    };
                    options.push(option);
                });

                deferred.resolve(options);
            });
            loader.load();
            return deferred.promise;
        }
    }
}