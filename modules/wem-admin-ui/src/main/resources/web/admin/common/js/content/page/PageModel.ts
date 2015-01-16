module api.content.page {

    import PropertyTree = api.data.PropertyTree;

    export class SetController {

        eventSource: any;

        descriptor: PageDescriptor;

        regions: api.content.page.region.Regions;

        config: PropertyTree;

        constructor(eventSource: any) {
            this.eventSource = eventSource;
        }

        setDescriptor(value: PageDescriptor): SetController {
            this.descriptor = value;
            return this;
        }

        setRegions(value: api.content.page.region.Regions): SetController {
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

        regions: api.content.page.region.Regions;

        config: PropertyTree;

        constructor(eventSource: any) {
            this.eventSource = eventSource;
        }

        setTemplate(template: PageTemplate, descriptor: PageDescriptor): SetTemplate {
            this.template = template;
            this.descriptor = descriptor;
            return this;
        }

        setRegions(value: api.content.page.region.Regions): SetTemplate {
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

        private controller: PageDescriptor;

        private template: PageTemplate;

        private templateDescriptor: PageDescriptor;

        private regions: api.content.page.region.Regions;

        private config: PropertyTree;

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        private ignorePropertyChanges: boolean = false;

        constructor(liveEditModel: api.liveedit.LiveEditModel, defaultTemplate: PageTemplate, defaultTemplateDescriptor: PageDescriptor) {
            this.liveEditModel = liveEditModel;
            this.defaultTemplate = defaultTemplate;
            this.defaultTemplateDescriptor = defaultTemplateDescriptor;
        }

        /**
         * Whether to ignore changes happening with properties (regions, properties) or not.
         */
        setIgnorePropertyChanges(value: boolean) {
            this.ignorePropertyChanges = value;
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
                    setRegions(api.content.page.region.Regions.create().build());
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
                this.setConfig(setController.config, setController.eventSource);
            }
            if (setController.regions) {
                this.setRegions(setController.regions, setController.eventSource);
            }

            if (this.regions && setController.descriptor) {
                this.regions.changeRegionsTo(setController.descriptor.getRegions());
            }

            var newControllerKey = this.controller ? this.controller.getKey() : null;
            if (!api.ObjectHelper.equals(oldControllerKey, newControllerKey)) {
                this.setIgnorePropertyChanges(true);
                this.notifyPropertyChanged("controller", oldControllerKey, newControllerKey, setController.eventSource);
                this.setIgnorePropertyChanges(false);
            }

            return this;
        }

        setAutomaticTemplate(eventSource?: any): PageModel {

            var config = this.defaultTemplate.hasConfig() ?
                         this.defaultTemplate.getConfig().copy() :
                         new PropertyTree(api.Client.get().getPropertyIdProvider());

            var regions = this.defaultTemplate.hasRegions() ?
                          this.defaultTemplate.getRegions().clone() :
                          api.content.page.region.Regions.create().build();

            var setTemplate = new SetTemplate(eventSource).
                setTemplate(null, this.defaultTemplateDescriptor).
                setRegions(regions).
                setConfig(config);

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
            this.templateDescriptor = setTemplate.descriptor;

            if (setTemplate.config) {
                this.setConfig(setTemplate.config, setTemplate.eventSource);
            }
            if (setTemplate.regions) {
                this.setRegions(setTemplate.regions, setTemplate.eventSource);
            }

            if (this.regions) {
                this.regions.changeRegionsTo(setTemplate.descriptor.getRegions());
            }

            var newTemplateKey = this.template ? this.template.getKey() : null;
            if (!api.ObjectHelper.equals(oldTemplateKey, newTemplateKey)) {
                this.setIgnorePropertyChanges(true);
                this.notifyPropertyChanged("template", oldTemplateKey, newTemplateKey, setTemplate.eventSource);
                this.setIgnorePropertyChanges(false);
            }
            return this;
        }

        setRegions(value: api.content.page.region.Regions, eventOrigin?: any): PageModel {
            var oldValue = this.regions;
            if (oldValue) {
                oldValue.unChanged(this.handleRegionsValueChanged);
            }
            
            this.regions = value;
            this.regions.onChanged(this.handleRegionsValueChanged.bind(this));

            this.setIgnorePropertyChanges(true);
            this.notifyPropertyChanged("regions", oldValue, value, eventOrigin);
            this.setIgnorePropertyChanges(false);
            return this;
        }

        private handleRegionsValueChanged(event: region.RegionsChangedEvent) {

            if (!this.ignorePropertyChanges) {
                console.log("PageModel.regions.onChanged: ", event);
                if (this.mode == PageMode.AUTOMATIC) {
                    var setTemplate = new SetTemplate(this).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                    this.setTemplate(setTemplate);
                }
            }
        }

        setConfig(value: PropertyTree, eventOrigin?: any): PageModel {
            var oldValue = this.config;
            if (oldValue) {
                oldValue.unChanged(this.handleConfigValueChanged);
            }

            this.config = value;
            this.config.onChanged(this.handleConfigValueChanged.bind(this));

            this.setIgnorePropertyChanges(true);
            this.notifyPropertyChanged("config", oldValue, value, eventOrigin);
            this.setIgnorePropertyChanges(false);
            return this;
        }

        private handleConfigValueChanged(event: api.data.PropertyEvent) {

            if (!this.ignorePropertyChanges) {
                console.log("PageModel.config.onChanged: ", event.getPath().toString());
                if (this.mode == PageMode.AUTOMATIC) {
                    var setTemplate = new SetTemplate(this).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                    this.setTemplate(setTemplate);
                }
            }
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

        getDefaultPageTemplate(): PageTemplate {
            return this.defaultTemplate;
        }

        getDefaultPageTemplateController(): PageDescriptor {
            return this.defaultTemplateDescriptor;
        }

        getMode(): PageMode {
            return this.mode;
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

        getTemplateDescriptor(): PageDescriptor {
            return this.templateDescriptor;
        }

        getRegions(): api.content.page.region.Regions {
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