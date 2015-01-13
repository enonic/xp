module api.content.page {

    import PropertyTree = api.data.PropertyTree;

    export class SetController {

        eventSource: any;

        descriptor: PageDescriptor;

        regions: PageRegions;

        config: PropertyTree;

        constructor(eventSource: any) {
            this.eventSource = eventSource;
        }

        setDescriptor(value: PageDescriptor): SetController {
            this.descriptor = value;
            return this;
        }

        setRegions(value: PageRegions): SetController {
            this.regions = value;
            return this;
        }

        setConfig(value: PropertyTree): SetController {
            this.config = value;
            return this;
        }
    }

    export class SetTemplate {

        eventSource: any;

        template: PageTemplate;

        descriptor: PageDescriptor;

        regions: PageRegions;

        config: PropertyTree;

        constructor(eventSource: any) {
            this.eventSource = eventSource;
        }

        setTemplate(template: PageTemplate, descriptor: PageDescriptor): SetTemplate {
            this.template = template;
            this.descriptor = descriptor;
            return this;
        }

        setRegions(value: PageRegions): SetTemplate {
            this.regions = value;
            return this;
        }

        setConfig(value: PropertyTree): SetTemplate {
            this.config = value;
            return this;
        }

    }

    export class PageModel {

        private liveEditModel: api.liveedit.LiveEditModel;

        private defaultTemplate: PageTemplate;

        private defaultTemplateDescriptor: PageDescriptor;

        private mode: PageMode;

        private initialized: boolean = false;

        private controller: PageDescriptor;

        private template: PageTemplate;

        private regions: PageRegions;

        private config: PropertyTree;

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor(liveEditModel: api.liveedit.LiveEditModel, defaultTemplate: PageTemplate, defaultTemplateDescriptor: PageDescriptor) {
            this.liveEditModel = liveEditModel;
            this.defaultTemplate = defaultTemplate;
            this.defaultTemplateDescriptor = defaultTemplateDescriptor;
        }

        getDefaultPageTemplate(): PageTemplate {
            return this.defaultTemplate;
        }

        getDefaultPageTemplateController(): PageDescriptor {
            return this.defaultTemplateDescriptor;
        }

        getMode(): PageMode {
            return this.mode;
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
                var setTemplate = new SetTemplate(eventSource).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                this.setTemplate(setTemplate);
            }
        }

        reset(eventSource?: any) {

            if (this.liveEditModel.getContent().isPageTemplate()) {
                var setController = new SetController(eventSource).
                    setDescriptor(null).
                    setConfig(new PropertyTree(api.Client.get().getPropertyIdProvider())).
                    setRegions(new PageRegionsBuilder().build());
                this.setController(setController);
            }
            else {
                this.setAutomaticTemplate(eventSource);
            }
        }

        setController(setController: SetController): PageModel {

            var oldControllerKey = this.controller ? this.controller.getKey() : null;
            this.controller = setController.descriptor;

            if (setController.descriptor) {
                this.mode = PageMode.FORCED_CONTROLLER;
            }
            else {
                this.mode = PageMode.NO_CONTROLLER;
            }

            if (setController.config) {
                this.setConfig(setController.config);
            }
            if (setController.regions) {
                this.setRegions(setController.regions);
            }

            if (this.regions && setController.descriptor) {
                this.regions.changeRegionsTo(setController.descriptor.getRegions());
            }

            var newControllerKey = this.controller ? this.controller.getKey() : null;
            if (!api.ObjectHelper.equals(oldControllerKey, newControllerKey)) {
                this.notifyPropertyChanged("controller", oldControllerKey, newControllerKey, setController.eventSource);
            }

            return this;
        }

        setAutomaticTemplate(eventSource?: any): PageModel {
            var setTemplate = new SetTemplate(eventSource);
            setTemplate.setTemplate(null, this.defaultTemplateDescriptor).
                setRegions(this.defaultTemplate.getRegions().clone()).
                setConfig(this.defaultTemplate.getConfig().copy());

            this.setTemplate(setTemplate);
            return this;
        }

        setTemplate(setTemplate: SetTemplate): PageModel {

            var oldTemplateKey = this.template ? this.template.getKey() : null;

            if (setTemplate.template) {
                this.mode = PageMode.FORCED_TEMPLATE;
            }
            else {
                this.mode = PageMode.AUTOMATIC;
            }

            this.template = setTemplate.template;

            if (setTemplate.config) {
                this.setConfig(setTemplate.config);
            }
            if (setTemplate.regions) {
                this.setRegions(setTemplate.regions);
            }

            if (this.regions) {
                this.regions.changeRegionsTo(setTemplate.descriptor.getRegions());
            }

            var newTemplateKey = this.template ? this.template.getKey() : null;
            if (!api.ObjectHelper.equals(oldTemplateKey, newTemplateKey)) {
                this.notifyPropertyChanged("template", oldTemplateKey, newTemplateKey, setTemplate.eventSource);
            }
            this.initialized = true;
            return this;
        }

        setRegions(value: PageRegions, eventOrigin?: any): PageModel {
            var oldValue = this.regions;
            this.regions = value;
            if (!api.ObjectHelper.equals(oldValue, value)) {
                this.notifyPropertyChanged("regions", oldValue, value, eventOrigin);
            }
            return this;
        }

        setConfig(value: PropertyTree, eventOrigin?: any): PageModel {

            var oldValue = this.config;

            this.config = value;
            this.config.onPropertyChanged((event: api.data.PropertyChangedEvent) => {
                if (this.mode == PageMode.AUTOMATIC) {
                    var setTemplate = new SetTemplate(this).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                    this.setTemplate(setTemplate);
                }
            });

            if (!api.ObjectHelper.equals(oldValue, value)) {
                this.notifyPropertyChanged("config", oldValue, value, eventOrigin);
            }
            return this;
        }

        /**
         * Automatic:           Content has no Page set. PageTemplate is automatically solved.
         *                      return: null;
         *
         * ForcedTemplate:      Content has a Page set with at Page.template set
         *
         * ForcedController:    Content has a Page set with at Page.controller set
         */
        getPage(): Page {

            if (this.mode == PageMode.AUTOMATIC) {

                return null;
            }
            else if (this.mode == PageMode.FORCED_TEMPLATE) {

                var regionsUnchanged = this.regions.equals(this.defaultTemplate.getRegions());
                var regions = regionsUnchanged ? null : this.regions;

                var configUnchanged = this.config.equals(this.defaultTemplate.getConfig());
                var config = configUnchanged ? null : this.config;

                return new PageBuilder().
                    setTemplate(this.getTemplateKey()).
                    setRegions(regions).
                    setConfig(config).
                    build();
            }
            else if (this.mode == PageMode.FORCED_CONTROLLER) {
                return new PageBuilder().
                    setController(this.controller.getKey()).
                    setRegions(this.regions).
                    setConfig(this.config).
                    build();
            }
            else if (this.mode == PageMode.NO_CONTROLLER) {
                return null;
            }
            else {
                throw new Error("Page mode not supported: " + this.mode);
            }

        }

        isPageTemplate(): boolean {
            return this.liveEditModel.getContent().isPageTemplate();
        }

        hasController(): boolean {
            return !!this.controller;
        }

        getController(): PageDescriptor {
            return this.controller;
        }

        hasTemplate(): boolean {
            return !!this.template;
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

        getConfig(): PropertyTree {
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
    }
}