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

            return LiveEditModelInitializer.initPageModel(this, this.content, defaultTemplate,
                defaultTemplateDescriptor).then((pageModel: PageModel) => {
                this.pageModel = pageModel;
            });
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

        isRenderableContent(): boolean {
            var hasController = this.pageModel.hasController(),
                hasDefaultPageTemplate = this.pageModel.hasDefaultPageTemplate(),
                hasApplications = this.siteModel.getApplicationKeys().length > 0;

            return hasApplications || hasController || hasDefaultPageTemplate;
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

    export class LiveEditModelInitializer {

        static initPageModel(liveEditModel: LiveEditModel, content: Content, defaultPageTemplate: PageTemplate,
                             defaultTemplateDescriptor: PageDescriptor): Q.Promise<PageModel> {

            var promises: wemQ.Promise<any>[] = [],
                pageMode = this.getPageMode(content, !!defaultPageTemplate),
                pageModel = new PageModel(liveEditModel, defaultPageTemplate, defaultTemplateDescriptor, pageMode);

            if (content.isPageTemplate()) {
                this.initPageTemplate(content, pageMode, pageModel, promises);
            }
            else {
                this.initPage(content, pageMode, pageModel, promises);
            }

            return this.resolvePromises(pageModel, promises);
        }

        private static initPageTemplate(content: Content, pageMode: PageMode, pageModel: PageModel, promises: wemQ.Promise<any>[]) {
            var pageTemplate = <PageTemplate>content;
            if (pageMode == PageMode.FORCED_CONTROLLER) {
                this.initForcedControllerPageTemplate(pageTemplate, pageModel, promises);
            }
            else if (pageMode == PageMode.NO_CONTROLLER) {
                this.initNoControllerPageTemplate(pageTemplate, pageModel);
            }
            else {
                throw new Error("Unsupported PageMode for a PageTemplate: " + pageMode);
            }
        }

        private static initPage(content: Content, pageMode: PageMode, pageModel: PageModel, promises: wemQ.Promise<any>[]) {
            const page = content.getPage();
            if (pageMode == PageMode.FORCED_TEMPLATE) {
                this.initForcedTemplatePage(content, page, pageModel, promises);
            }
            else if (pageMode == PageMode.FORCED_CONTROLLER) {
                this.initForcedControllerPage(page, pageModel, promises)
            }
            else if (pageMode == PageMode.AUTOMATIC) {
                pageModel.setAutomaticTemplate(this);
            }
            else if (pageMode == PageMode.NO_CONTROLLER || pageMode == PageMode.FRAGMENT) {
                this.initNoControllerPage(pageModel);
            }
            else {
                throw new Error("Unsupported PageMode for a Content: " + PageMode[<number>pageMode]);
            }
        }

        private static initForcedControllerPageTemplate(pageTemplate: PageTemplate, pageModel: PageModel, promises: wemQ.Promise<any>[]) {
            var pageDescriptorKey = pageTemplate.getController(),
                pageDescriptorPromise: wemQ.Promise<PageDescriptor> = this.loadPageDescriptor(pageDescriptorKey);
            pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {

                var config = pageTemplate.hasConfig() ?
                             pageTemplate.getPage().getConfig().copy() :
                             new PropertyTree();

                var regions = pageTemplate.hasRegions() ?
                              pageTemplate.getRegions().clone() :
                              Regions.create().build();

                var setController = new SetController(this).setDescriptor(pageDescriptor).setConfig(config).setRegions(regions);
                pageModel.initController(setController);
            });

            promises.push(pageDescriptorPromise);
        }

        private static initNoControllerPageTemplate(pageTemplate: PageTemplate, pageModel: PageModel) {
            var config = pageTemplate.hasConfig() ?
                         pageTemplate.getConfig().copy() :
                         new PropertyTree();

            var regions = pageTemplate.hasRegions() ?
                          pageTemplate.getRegions().clone() :
                          Regions.create().build();

            var setController = new SetController(this).setDescriptor(null).setConfig(config).setRegions(regions);
            pageModel.initController(setController);
        }

        private static initForcedTemplatePage(content: Content, page: api.content.page.Page, pageModel: PageModel,
                                              promises: wemQ.Promise<any>[]) {
            var pageTemplateKey = page.getTemplate(),
                pageTemplatePromise: wemQ.Promise<PageTemplate> = this.loadPageTemplate(pageTemplateKey),
                pageDescriptorPromise: wemQ.Promise<PageDescriptor>;

            pageTemplatePromise.then((pageTemplate: PageTemplate) => {

                var pageDescriptorKey = pageTemplate.getController();
                pageDescriptorPromise = this.loadPageDescriptor(pageDescriptorKey);
                pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {

                    var config = content.getPage().hasConfig() ?
                                 content.getPage().getConfig().copy() :
                                 pageTemplate.getConfig().copy();

                    var regions = content.getPage().hasRegions() ?
                                  content.getPage().getRegions().clone() :
                                  pageTemplate.getRegions().clone();


                    var setTemplate = new SetTemplate(this).setTemplate(pageTemplate, pageDescriptor).setRegions(regions).setConfig(config);
                    pageModel.initTemplate(setTemplate);
                });
                promises.push(pageDescriptorPromise);
            });
            promises.push(pageTemplatePromise);
        }

        private static initForcedControllerPage(page: api.content.page.Page, pageModel: PageModel, promises: wemQ.Promise<any>[]) {
            var pageDescriptorKey = page.getController();

            if (pageDescriptorKey) {
                var pageDescriptorPromise = this.loadPageDescriptor(pageDescriptorKey);
                pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {
                    this.initPageController(page, pageModel, pageDescriptor);
                });
                promises.push(pageDescriptorPromise);
            }
            else {
                this.initPageController(page, pageModel, null);
            }
        }

        private static initNoControllerPage(pageModel: PageModel) {
            var config = new PropertyTree();

            var regions = Regions.create().build();

            var setController = new SetController(this).setDescriptor(null).setConfig(config).setRegions(regions);
            pageModel.initController(setController);
        }

        private static initPageController(page: api.content.page.Page, pageModel: PageModel, pageDescriptor: PageDescriptor) {

            var config = page.hasConfig() ?
                         page.getConfig().copy() :
                         new PropertyTree();

            var regions = page.hasRegions() ?
                          page.getRegions().clone() :
                          Regions.create().build();

            var setController = new SetController(this).setDescriptor(pageDescriptor).setConfig(config).setRegions(regions);

            pageModel.initController(setController);
        }


        private static getPageMode(content: Content, defaultTemplatePresents: boolean): api.content.page.PageMode {
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

        private static loadPageTemplate(key: PageTemplateKey): wemQ.Promise<PageTemplate> {
            var deferred = wemQ.defer<PageTemplate>();
            new GetPageTemplateByKeyRequest(key).sendAndParse().then((pageTemplate: PageTemplate) => {
                deferred.resolve(pageTemplate);
            }).catch((reason) => {
                deferred.reject(new api.Exception("Page template '" + key + "' not found.", api.ExceptionType.WARNING));
            }).done();
            return deferred.promise;
        }

        private static loadPageDescriptor(key: DescriptorKey): wemQ.Promise<PageDescriptor> {
            var deferred = wemQ.defer<PageDescriptor>();
            new GetPageDescriptorByKeyRequest(key).sendAndParse().then((pageDescriptor: PageDescriptor) => {
                deferred.resolve(pageDescriptor);
            }).catch((reason) => {
                deferred.reject(new api.Exception("Page descriptor '" + key + "' not found.", api.ExceptionType.WARNING));
            }).done();
            return deferred.promise;
        }

        private static resolvePromises(pageModel: PageModel, promises: wemQ.Promise<any>[]): Q.Promise<PageModel> {
            var deferred = wemQ.defer<PageModel>();

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
    }
}