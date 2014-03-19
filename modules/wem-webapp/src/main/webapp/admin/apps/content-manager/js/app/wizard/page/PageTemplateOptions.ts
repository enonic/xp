module app.wizard.page {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
    import PageTemplateSummary = api.content.page.PageTemplateSummary;
    import PageTemplateSummaryLoader = api.content.page.PageTemplateSummaryLoader;
    import Option = api.ui.selector.Option;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PageTemplateOptions {

        private siteTemplateKey: SiteTemplateKey;

        private contentType: ContentTypeName;

        private static defaultPageTemplateOption: Option<PageTemplateOption> = {value: "__auto__", displayValue: new PageTemplateOption(null)};

        constructor(siteTemplateKey: SiteTemplateKey, contentType: ContentTypeName) {
            this.siteTemplateKey = siteTemplateKey;
            this.contentType = contentType;
        }

        static getDefault(): Option<PageTemplateOption> {
            return this.defaultPageTemplateOption;
        }

        getOptions(): Q.Promise<Option<PageTemplateOption>[]> {

            var deferred = Q.defer<Option<PageTemplateOption>[]>();

            var options: Option<PageTemplateOption>[] = [];
            options.push(PageTemplateOptions.defaultPageTemplateOption);

            var loader = new PageTemplateSummaryLoader(new GetPageTemplatesByCanRenderRequest(this.siteTemplateKey,
                this.contentType));

            loader.onLoadedData((event: LoadedDataEvent<PageTemplateSummary>) => {

                var pageTemplates: PageTemplateSummary[] = event.getData();

                pageTemplates.forEach((pageTemplate: PageTemplateSummary, index: number) => {

                    var option = {value: pageTemplate.getKey().toString(), displayValue: new PageTemplateOption(pageTemplate)};
                    options.push(option);
                });

                deferred.resolve(options);
            });
            return deferred.promise;
        }
    }
}