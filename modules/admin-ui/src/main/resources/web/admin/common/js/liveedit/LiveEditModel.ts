module api.liveedit {

    import PropertyTree = api.data.PropertyTree;
    import Content = api.content.Content;
    import Descriptor = api.content.page.Descriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import SetTemplate = api.content.page.SetTemplate;
    import PageModel = api.content.page.PageModel;
    import SetController = api.content.page.SetController;
    import Regions = api.content.page.region.Regions;
    import PageMode = api.content.page.PageMode;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import SiteModel = api.content.site.SiteModel;
    import ContentFormContext = api.content.form.ContentFormContext;

    export class LiveEditModel {

        private siteModel: SiteModel;

        private parentContent: Content;

        private content: Content;

        private formContext: ContentFormContext;

        private pageModel: PageModel;

        constructor(builder: LiveEditModelBuilder) {
            this.siteModel = builder.siteModel;
            this.parentContent = builder.parentContent;
            this.content = builder.content;
            this.formContext = builder.formContext;
        }

        init(defaultTemplate: PageTemplate, defaultTemplateDescriptor: PageDescriptor): wemQ.Promise<void> {

            return this.initPageModel(defaultTemplate, defaultTemplateDescriptor).then((pageModel: PageModel) => {

                this.pageModel = pageModel;
            });
        }

        private initPageModel(defaultPageTemplate: PageTemplate, defaultTemplateDescriptor: PageDescriptor): Q.Promise<PageModel> {

            var deferred = wemQ.defer<PageModel>();

            var pageModel = new PageModel(this, defaultPageTemplate, defaultTemplateDescriptor);
            var pageMode = this.content.getPageMode();

            var pageDescriptorPromise: wemQ.Promise<PageDescriptor> = null;
            var pageTemplatePromise: wemQ.Promise<PageTemplate> = null;

            if (this.content.isPageTemplate()) {
                var pageTemplate = <PageTemplate>this.content;
                if (pageMode == PageMode.FORCED_CONTROLLER) {

                    var pageDescriptorKey = pageTemplate.getController();
                    pageDescriptorPromise = this.loadPageDescriptor(pageDescriptorKey);
                    pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {

                        var config = pageTemplate.hasConfig() ?
                                     pageTemplate.getPage().getConfig().copy() :
                                     new PropertyTree(api.Client.get().getPropertyIdProvider());

                        var regions = pageTemplate.hasRegions() ?
                                      pageTemplate.getRegions().clone() :
                                      Regions.create().build();

                        var setController = new SetController(this).
                            setDescriptor(pageDescriptor).
                            setConfig(config).
                            setRegions(regions);
                        pageModel.setController(setController);
                    });
                }
                else if (pageMode == PageMode.NO_CONTROLLER) {

                    var config = pageTemplate.hasConfig() ?
                                 pageTemplate.getConfig().copy() :
                                 new PropertyTree(api.Client.get().getPropertyIdProvider());

                    var regions = pageTemplate.hasRegions() ?
                                  pageTemplate.getRegions().clone() :
                                  Regions.create().build();

                    var setController = new SetController(this).
                        setDescriptor(null).
                        setConfig(config).
                        setRegions(regions);
                    pageModel.setController(setController);
                }
                else {
                    throw new Error("Unsupported PageMode for a PageTemplate: " + pageMode);
                }
            }
            else {
                if (pageMode == PageMode.FORCED_TEMPLATE) {

                    var pageTemplateKey = this.content.getPage().getTemplate();
                    pageTemplatePromise = this.loadPageTemplate(pageTemplateKey);
                    pageTemplatePromise.then((pageTemplate: PageTemplate) => {

                        var pageDescriptorKey = pageTemplate.getController();
                        pageDescriptorPromise = this.loadPageDescriptor(pageDescriptorKey);
                        pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {

                            var config = this.content.getPage().hasConfig() ?
                                         this.content.getPage().getConfig().copy() :
                                         pageTemplate.getConfig().copy();

                            var regions = this.content.getPage().hasRegions() ?
                                          this.content.getPage().getRegions().clone() :
                                          pageTemplate.getRegions().clone();


                            var setTemplate = new SetTemplate(this).
                                setTemplate(pageTemplate, pageDescriptor).
                                setRegions(regions).
                                setConfig(config);
                            pageModel.setTemplate(setTemplate);
                        });
                    });
                }
                else if (pageMode == PageMode.AUTOMATIC) {
                    pageModel.setAutomaticTemplate(this);
                }
                else {
                    throw new Error("Unsupported PageMode for a Content: " + pageMode);
                }
            }

            var promises: wemQ.Promise<any>[] = [];
            if (pageDescriptorPromise) {
                promises.push(pageDescriptorPromise);
            }
            if (pageTemplatePromise) {
                promises.push(pageTemplatePromise);
            }
            if (promises.length > 0) {
                wemQ.all(promises).then(() => {

                    deferred.resolve(pageModel);

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }
            else {
                deferred.resolve(pageModel);
            }

            return deferred.promise;
        }

        private loadPageTemplate(key: PageTemplateKey): wemQ.Promise<PageTemplate> {
            return new GetPageTemplateByKeyRequest(key).sendAndParse();
        }

        private loadPageDescriptor(key: DescriptorKey): wemQ.Promise<PageDescriptor> {
            return new GetPageDescriptorByKeyRequest(key).sendAndParse();
        }

        setContent(value: Content) {
            this.content = value;
        }

        getParentContent(): Content {
            return this.parentContent;
        }

        getFormContext(): ContentFormContext {
            return this.formContext;
        }

        getContent(): Content {
            return this.content;
        }

        getSiteModel(): SiteModel {
            return this.siteModel;
        }

        getPageModel(): PageModel {
            return this.pageModel;
        }

        static create(): LiveEditModelBuilder {
            return new LiveEditModelBuilder();
        }
    }

    class LiveEditModelBuilder {

        siteModel: SiteModel;

        parentContent: Content;

        content: Content;

        formContext: ContentFormContext;

        setSiteModel(value: SiteModel): LiveEditModelBuilder {
            this.siteModel = value;
            return this;
        }

        setParentContent(value: Content): LiveEditModelBuilder {
            this.parentContent = value;
            return this;
        }

        setContent(value: Content): LiveEditModelBuilder {
            this.content = value;
            return this;
        }

        setContentFormContext(value: ContentFormContext): LiveEditModelBuilder {
            this.formContext = value;
            return this;
        }

        build(): LiveEditModel {
            return new LiveEditModel(this);
        }

    }
}