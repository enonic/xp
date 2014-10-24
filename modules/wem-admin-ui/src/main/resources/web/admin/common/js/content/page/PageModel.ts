module api.content.page {

    import RootDataSet = api.data.RootDataSet;

    export class PageModel {

        private liveEditModel: api.liveedit.LiveEditModel;

        private defaultPageTemplate: PageTemplate;

        private initialized: boolean = false;

        private controller: PageDescriptor;

        private template: PageTemplate;

        private usingDefaultTemplate: boolean = false;

        private regions: PageRegions;

        private config: RootDataSet;

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor(liveEditModel: api.liveedit.LiveEditModel, defaultPageTemplate: PageTemplate) {
            this.liveEditModel = liveEditModel;
            this.defaultPageTemplate = defaultPageTemplate;
        }

        initialize(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            if (!this.initialized) {

                var pageDescriptorPromise: wemQ.Promise<PageDescriptor> = null;
                var pageTemplatePromise: wemQ.Promise<PageTemplate> = null;


                if (this.liveEditModel.getContent().isPageTemplate()) {
                    var pageDescriptorKey = null;
                    if (this.liveEditModel.getContent().isPage()) {
                        pageDescriptorKey = this.liveEditModel.getContent().getPage().getController();
                        pageDescriptorPromise = this.loadPageDescriptor(pageDescriptorKey);
                        pageDescriptorPromise.then((pageDescriptor: PageDescriptor) => {
                            this.setController(pageDescriptor, this);
                        });
                    }
                    else {
                        this.setController(null, this);
                    }
                }
                else {

                    if (this.liveEditModel.getContent().isPage()) {
                        var pageTemplateKey = this.liveEditModel.getContent().getPage().getTemplate();
                        pageTemplatePromise = this.loadPageTemplate(pageTemplateKey);
                        pageTemplatePromise.then((pageTemplate: PageTemplate) => {
                            this.setTemplate(pageTemplate, this.liveEditModel.getContent().getPage(), this);
                        });
                    }
                    else {
                        if (this.defaultPageTemplate) {
                            this.setDefaultTemplate(this);
                        }
                    }
                }
                var promises: wemQ.Promise<any>[] = [];
                if (pageDescriptorPromise) {
                    promises.push(pageDescriptorPromise);
                }
                if (pageTemplatePromise) {
                    promises.push(pageTemplatePromise);
                }
                wemQ.all(promises).then(() => {
                    deferred.resolve(null);

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }

            return deferred.promise;
        }

        setController(pageDescriptor: PageDescriptor, eventSource?: any): PageModel {

            var oldControllerKey = this.controller ? this.controller.getKey() : null;
            this.controller = pageDescriptor;
            this.usingDefaultTemplate = false;

            if (!this.initialized) {
                var content = this.liveEditModel.getContent();
                this.regions = content.isPage() ? content.getPage().getRegions() : new PageRegionsBuilder().build();
                this.config = content.isPage() ? content.getPage().getConfig() : new RootDataSet();
                this.initialized = true;
            }
            else {
                var regionDescriptors = pageDescriptor.getRegions();
                this.regions.changeRegionsTo(regionDescriptors);
            }

            var newControllerKey = this.controller ? this.controller.getKey() : null;
            if (!api.ObjectHelper.equals(oldControllerKey, newControllerKey)) {
                this.notifyPropertyChanged("controller", oldControllerKey, newControllerKey, eventSource);
            }

            return this;
        }

        setDefaultTemplate(eventSource?: any): PageModel {

            var oldTemplateKey = this.template ? this.template.getKey() : null;
            this.template = null;
            this.usingDefaultTemplate = true;

            // Need to clone config objects from template, otherwise the template gets changed while editing, since DataSet's are mutable
            this.regions = this.defaultPageTemplate.getRegions().clone();
            this.config = this.defaultPageTemplate.getConfig().clone();

            if (!api.ObjectHelper.equals(oldTemplateKey, null)) {
                this.notifyPropertyChanged("template", oldTemplateKey, null, eventSource);
            }
            this.initialized = true;

            return this;
        }

        initializePageFromDefault(eventSource?: any) {

            var skip = false;
            if (this.hasTemplate()) {
                skip = true;
            }
            if (this.hasController()) {
                skip = true;
            }

            if (!skip) {
                this.setTemplate(this.defaultPageTemplate, this.liveEditModel.getContent().getPage(), eventSource);
            }
        }

        setTemplate(template: PageTemplate, page: Page, eventSource?: any): PageModel {
            api.util.assertNotNull(template, "template cannot be null");
            var oldTemplateKey = this.template ? this.template.getKey() : null;
            this.template = template;
            this.usingDefaultTemplate = false;

            if (!this.initialized) {
                if (page) {
                    this.regions = page.getRegions();
                    this.config = page.getConfig();
                }
                else {
                    // Need to clone config objects from template, otherwise the template gets changed while editing, since DataSet's are mutable
                    this.regions = template.getRegions().clone();
                    this.config = template.getConfig().clone();
                }
            }
            else {


            }
            var newTemplateKey = this.template ? this.template.getKey() : null;
            if (!api.ObjectHelper.equals(oldTemplateKey, newTemplateKey)) {
                this.notifyPropertyChanged("template", oldTemplateKey, newTemplateKey, eventSource);
            }
            this.initialized = true;
            return this;
        }

        setConfig(value: RootDataSet, eventOrigin?: any): PageModel {
            var oldValue = this.config;
            this.config = value;
            if (!api.ObjectHelper.equals(oldValue, value)) {
                this.notifyPropertyChanged("config", oldValue, value, eventOrigin);
            }
            return this;
        }

        getPage(): Page {

            if (this.isUsingDefaultTemplate()) {
                var defaultPage = this.defaultPageTemplate.getPage();
                var regionsChanges = !this.regions.equals(defaultPage.getRegions());
                var configChanges = !this.config.equals(defaultPage.getConfig());

                if (!regionsChanges && !configChanges) {
                    return null;
                }
                else {
                    var oldTemplateKey = this.template ? this.template.getKey() : null;
                    this.template = this.defaultPageTemplate;
                    var newTemplateKey = this.template ? this.template.getKey() : null;
                    if (!api.ObjectHelper.equals(oldTemplateKey, newTemplateKey)) {
                        this.notifyPropertyChanged("template", oldTemplateKey, newTemplateKey, this);
                    }
                }
            }
            else {
                if (!this.hasTemplate() && !this.hasController()) {
                    return null;
                }
            }

            return new PageBuilder().
                setController(this.getControllerKey()).
                setTemplate(this.getTemplateKey()).
                setRegions(this.regions).
                setConfig(this.config).
                build();
        }

        isPageTemplate(): boolean {
            return this.liveEditModel.getContent().isPageTemplate();
        }

        hasController(): boolean {
            return !!this.controller;
        }

        getControllerKey(): DescriptorKey {
            return this.controller ? this.controller.getKey() : null;
        }

        getController(): PageDescriptor {
            return this.controller;
        }

        hasTemplate(): boolean {
            return !!this.template;
        }

        isUsingDefaultTemplate(): boolean {
            return this.usingDefaultTemplate;
        }

        getTemplateKey(): PageTemplateKey {
            return this.template ? this.template.getKey() : null;
        }

        getTemplate(): PageTemplate {
            return this.template;
        }

        getRegions(): PageRegions {
            return this.regions;
        }

        getConfig(): RootDataSet {
            return this.config;
        }

        onPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr: (event: api.PropertyChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyPropertyChanged(property: string, oldValue: any, newValue: any, origin: any) {
            var event = new api.PropertyChangedEvent(property, oldValue, newValue, origin);
            this.propertyChangedListeners.forEach((listener: (event: api.PropertyChangedEvent)=>void) => {
                listener(event);
            })
        }

        private loadPageTemplate(key: PageTemplateKey): wemQ.Promise<PageTemplate> {
            return new GetPageTemplateByKeyRequest(key).sendAndParse();
        }

        private loadPageDescriptor(key: DescriptorKey): wemQ.Promise<PageDescriptor> {
            return new GetPageDescriptorByKeyRequest(key).sendAndParse();
        }
    }
}