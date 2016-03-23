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

            var pageMode = this.getPageMode(this.content, !!defaultPageTemplate);
            var pageModel = new PageModel(this, defaultPageTemplate, defaultTemplateDescriptor, pageMode);

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
                                     new PropertyTree();

                        var regions = pageTemplate.hasRegions() ?
                                      pageTemplate.getRegions().clone() :
                                      Regions.create().build();

                        var setController = new SetController(this).
                            setDescriptor(pageDescriptor).
                            setConfig(config).
                            setRegions(regions);
                        pageModel.initController(setController);
                    });
                }
                else if (pageMode == PageMode.NO_CONTROLLER) {

                    var config = pageTemplate.hasConfig() ?
                                 pageTemplate.getConfig().copy() :
                                 new PropertyTree();

                    var regions = pageTemplate.hasRegions() ?
                                  pageTemplate.getRegions().clone() :
                                  Regions.create().build();

                    var setController = new SetController(this).
                        setDescriptor(null).
                        setConfig(config).
                        setRegions(regions);
                    pageModel.initController(setController);
                }
                else {
                    throw new Error("Unsupported PageMode for a PageTemplate: " + pageMode);
                }
            }
            else {
                var page = this.content.getPage();
                if (pageMode == PageMode.FORCED_TEMPLATE) {

                    var pageTemplateKey = page.getTemplate();
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
                            pageModel.initTemplate(setTemplate);
                        });
                    });
                }
                else if (pageMode == PageMode.FORCED_CONTROLLER) {

                    var pageDescriptorKey = page.getController();
                    pageDescriptorPromise = this.loadPageDescriptor(pageDescriptorKey);
                    pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {

                        var config = page.hasConfig() ?
                                     page.getConfig().copy() :
                                     new PropertyTree();

                        var regions = page.hasRegions() ?
                                      page.getRegions().clone() :
                                      Regions.create().build();

                        var setController = new SetController(this).
                            setDescriptor(pageDescriptor).
                            setConfig(config).
                            setRegions(regions);
                        pageModel.initController(setController);
                    });
                }
                else if (pageMode == PageMode.AUTOMATIC) {
                    pageModel.setAutomaticTemplate(this);
                }
                else if (pageMode == PageMode.NO_CONTROLLER || pageMode == PageMode.FRAGMENT) {
                    var config = new PropertyTree();

                    var regions = Regions.create().build();

                    var setController = new SetController(this).
                        setDescriptor(null).
                        setConfig(config).
                        setRegions(regions);
                    pageModel.initController(setController);
                }
                else {
                    throw new Error("Unsupported PageMode for a Content: " + PageMode[pageMode]);
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
                    deferred.reject(reason);
                }).done();
            }
            else {
                deferred.resolve(pageModel);
            }

            return deferred.promise;
        }

        private getPageMode(content: Content, defaultTemplatePresents: boolean): api.content.page.PageMode {
            if (content.getType().isFragment()) {
                return api.content.page.PageMode.FRAGMENT;

            } else if (content.isPage()) {
                if (content.getPage().hasTemplate()) {
                    //in case content's template was deleted or updated to not support content's type
                    if (defaultTemplatePresents) {
                        return api.content.page.PageMode.FORCED_TEMPLATE;
                    }
                    else {
                        return api.content.page.PageMode.NO_CONTROLLER;
                    }
                }
                else {
                    return api.content.page.PageMode.FORCED_CONTROLLER;
                }
            }
            else if (defaultTemplatePresents) {
                return api.content.page.PageMode.AUTOMATIC;
            }
            else {
                return api.content.page.PageMode.NO_CONTROLLER;
            }
        }

        private loadPageTemplate(key: PageTemplateKey): wemQ.Promise<PageTemplate> {
            var deferred = wemQ.defer<PageTemplate>();
            new GetPageTemplateByKeyRequest(key).sendAndParse().
                then((pageTemplate: PageTemplate) => {
                    deferred.resolve(pageTemplate);
                }).catch((reason) => {
                    deferred.reject(new api.Exception("Page template '" + key + "' not found.", api.ExceptionType.WARNING));
                }).done();
            return deferred.promise;
        }

        private loadPageDescriptor(key: DescriptorKey): wemQ.Promise<PageDescriptor> {
            var deferred = wemQ.defer<PageDescriptor>();
            new GetPageDescriptorByKeyRequest(key).sendAndParse().
                then((pageDescriptor: PageDescriptor) => {
                    deferred.resolve(pageDescriptor);
                }).catch((reason) => {
                    deferred.reject(new api.Exception("Page descriptor '" + key + "' not found.", api.ExceptionType.WARNING));
                }).done();
            return deferred.promise;
        }

        isPageRenderable(): boolean {
            return !!this.pageModel && (this.pageModel.hasController() ||
                                        this.pageModel.getMode() != api.content.page.PageMode.NO_CONTROLLER);
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

    export class LiveEditModelBuilder {

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