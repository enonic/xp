module api.content.page {

    import PropertyTree = api.data.PropertyTree;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;

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

        public static PROPERTY_REGIONS: string = 'regions';

        public static PROPERTY_TEMPLATE: string = 'template';

        public static PROPERTY_CONTROLLER: string = 'controller';

        public static PROPERTY_CONFIG: string = 'config';

        private liveEditModel: api.liveedit.LiveEditModel;

        private defaultTemplate: PageTemplate;

        private defaultTemplateDescriptor: PageDescriptor;

        private mode: PageMode;

        private controller: PageDescriptor;

        private template: PageTemplate;

        private templateDescriptor: PageDescriptor;

        private regions: api.content.page.region.Regions;

        private fragment: api.content.page.region.Component;

        private config: PropertyTree;

        private pageModeChangedListeners: {(event: PageModeChangedEvent):void}[] = [];

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        private componentPropertyChangedListeners: {(event: ComponentPropertyChangedEvent):void}[] = [];

        private customizeChangedListeners: {(value: boolean): void}[] = [];

        private resetListeners: {():void}[] = [];

        private ignorePropertyChanges: boolean = false;

        private componentPropertyChangedEventHandler: (event: ComponentPropertyChangedEvent) => void;

        private regionsChangedEventHandler: () => void;

        private configPropertyChangedHandler: () => void;

        private customized: boolean;

        constructor(liveEditModel: api.liveedit.LiveEditModel, defaultTemplate: PageTemplate, defaultTemplateDescriptor: PageDescriptor,
                    pageMode: api.content.page.PageMode) {
            this.liveEditModel = liveEditModel;
            this.defaultTemplate = defaultTemplate;
            this.defaultTemplateDescriptor = defaultTemplateDescriptor;
            this.mode = pageMode;
            this.customized = liveEditModel.getContent().isPage() && liveEditModel.getContent().getPage().isCustomized();
            this.fragment = liveEditModel.getContent().getPage() ? liveEditModel.getContent().getPage().getFragment() : null;
            this.configPropertyChangedHandler = () => {
                if (!this.ignorePropertyChanges) {
                    if (this.mode === PageMode.AUTOMATIC) {
                        let setTemplate = new SetTemplate(this).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                        this.setTemplate(setTemplate, true);
                    }
                }
            };
            this.regionsChangedEventHandler = () => {
                if (!this.ignorePropertyChanges) {
                    if (this.mode === PageMode.AUTOMATIC) {
                        let setTemplate = new SetTemplate(this).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                        this.setTemplate(setTemplate);
                    }
                }
            };
            this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {
                if (!this.isPageTemplate() && this.getMode() === PageMode.AUTOMATIC) {
                    this.initializePageFromDefault(this);
                }

                this.componentPropertyChangedListeners.forEach((listener: (event: ComponentPropertyChangedEvent) => void) => {
                    listener(event);
                });
            };
        }

        /**
         * Whether to ignore changes happening with properties (regions, properties) or not.
         */
        setIgnorePropertyChanges(value: boolean) {
            this.ignorePropertyChanges = value;
        }

        initializePageFromDefault(eventSource?: any) {

            let skip = false;
            if (this.hasTemplate()) {
                skip = true;
            }
            if (this.hasController()) {
                skip = true;
            }

            if (!skip) {
                let setTemplate = new SetTemplate(eventSource).setTemplate(this.defaultTemplate, this.defaultTemplateDescriptor);
                this.setTemplate(setTemplate);
            }
        }

        setCustomized(value: boolean) {
            const oldValue = this.customized;

            this.customized = value;

            if (oldValue !== value) {
                this.notifyCustomizeChanged(this.customized);
            }
        }

        reset(eventSource?: any) {
            if (this.isPageTemplate() || !this.defaultTemplate) {
                let setController = new SetController(eventSource).
                    setDescriptor(null).
                    setConfig(new PropertyTree()).
                    setRegions(api.content.page.region.Regions.create().build());
                this.setController(setController);
            } else {
                this.setAutomaticTemplate(eventSource);
            }

            this.setCustomized(false);

            this.notifyReset();
        }

        private setMode(value: PageMode) {
            let oldValue = this.mode;

            this.mode = value;

            if (this.mode !== oldValue) {
                this.notifyPageModeChanged(oldValue, this.mode);
            }
        }

        setController(setController: SetController): PageModel {
            let oldControllerKey = this.controller ? this.controller.getKey() : null;
            let newControllerKey = setController.descriptor ? setController.descriptor.getKey() : null;
            let controllerChanged = !api.ObjectHelper.equals(oldControllerKey, newControllerKey);

            this.setControllerData(setController);

            this.setMode(setController.descriptor ? PageMode.FORCED_CONTROLLER : PageMode.NO_CONTROLLER);

            if (!this.isPageTemplate()) {
                this.setCustomized(true);
            }

            if (!oldControllerKey && this.templateDescriptor) {
                oldControllerKey = this.templateDescriptor.getKey();
            }

            this.template = null;

            if (controllerChanged) {
                this.setIgnorePropertyChanges(true);
                this.notifyPropertyChanged(PageModel.PROPERTY_CONTROLLER, oldControllerKey, newControllerKey, setController.eventSource);
                this.setIgnorePropertyChanges(false);
            }

            return this;
        }

        initController(setController: SetController): PageModel {

            this.setControllerData(setController);

            return this;
        }

        private setControllerData(setController: SetController) {
            this.controller = setController.descriptor;

            if (setController.config) {
                this.setConfig(setController.config, setController.eventSource);
            }
            if (setController.regions) {
                this.setRegions(setController.regions, setController.eventSource);
            }

            if (this.regions && setController.descriptor) {
                this.regions.changeRegionsTo(setController.descriptor.getRegions());
            }

            if (this.fragment) {
                this.unregisterFragmentListeners(this.fragment);
                this.registerFragmentListeners(this.fragment);
            }
        }

        setTemplateContoller() {
            this.setController(
                new SetController(this).
                setDescriptor(this.templateDescriptor || this.getDefaultPageDescriptor())
            );
        }

        setAutomaticTemplate(eventSource?: any, ignoreRegionChanges: boolean = false): PageModel {

            let config = this.defaultTemplate.hasConfig() ?
                         this.defaultTemplate.getConfig().copy() :
                         new PropertyTree();

            let regions = this.defaultTemplate.hasRegions() ?
                          this.defaultTemplate.getRegions().clone() :
                          api.content.page.region.Regions.create().build();

            let setTemplate = new SetTemplate(eventSource).
                setTemplate(null, this.defaultTemplateDescriptor).
                setRegions(regions).
                setConfig(config);

            this.setTemplate(setTemplate, ignoreRegionChanges);
            return this;
        }

        setTemplate(setTemplate: SetTemplate, ignoreRegionChanges: boolean = false): PageModel {
            let oldTemplateKey = this.template ? this.template.getKey() : null;
            let newTemplateKey = setTemplate.template ? setTemplate.template.getKey() : null;
            let templateOrModeChanged = !api.ObjectHelper.equals(oldTemplateKey, newTemplateKey) || this.hasController();

            if (setTemplate.template) {
                this.setMode(PageMode.FORCED_TEMPLATE);
            } else if (this.getMode() !== PageMode.FRAGMENT) {
                this.setMode(PageMode.AUTOMATIC);
            }

            this.setTemplateData(setTemplate, ignoreRegionChanges);

            if (templateOrModeChanged) {
                this.setIgnorePropertyChanges(true);
                this.notifyPropertyChanged(PageModel.PROPERTY_TEMPLATE, oldTemplateKey, newTemplateKey, setTemplate.eventSource);
                this.setIgnorePropertyChanges(false);
            }

            this.controller = null;

            return this;
        }

        initTemplate(setTemplate: SetTemplate, ignoreRegionChanges: boolean = false): PageModel {

            this.setTemplateData(setTemplate, ignoreRegionChanges);

            return this;
        }

        private setTemplateData(setTemplate: SetTemplate, ignoreRegionChanges: boolean = false) {
            this.template = setTemplate.template;
            this.templateDescriptor = setTemplate.descriptor;

            if (setTemplate.config) {
                this.setConfig(setTemplate.config, setTemplate.eventSource);
            }
            if (setTemplate.regions) {
                this.setRegions(setTemplate.regions, setTemplate.eventSource, ignoreRegionChanges);
            } else if (setTemplate.template && setTemplate.template.hasRegions()) {
                // copy regions to avoid modifying defaultTemplate regions
                this.setRegions(setTemplate.template.getRegions().clone(), setTemplate.eventSource, ignoreRegionChanges);
            }

            if (this.regions) {
                let regions = setTemplate.descriptor ? setTemplate.descriptor.getRegions() : [];
                this.regions.changeRegionsTo(regions);
            }
        }

        setRegions(value: api.content.page.region.Regions, eventOrigin?: any, ignoreRegionChanges: boolean = false): PageModel {
            let oldValue = this.regions;
            if (oldValue) {
                this.unregisterRegionsListeners(oldValue);
            }

            this.regions = value;
            this.registerRegionsListeners(this.regions);

            this.setIgnorePropertyChanges(true);
            if (!ignoreRegionChanges) {
                this.notifyPropertyChanged(PageModel.PROPERTY_REGIONS, oldValue, value, eventOrigin);
            }
            this.setIgnorePropertyChanges(false);
            return this;
        }

        setConfig(value: PropertyTree, eventOrigin?: any): PageModel {
            let oldValue = this.config;
            if (oldValue) {
                oldValue.unChanged(this.configPropertyChangedHandler);
            }

            this.config = value;
            this.config.onChanged(this.configPropertyChangedHandler);

            this.setIgnorePropertyChanges(true);
            this.notifyPropertyChanged(PageModel.PROPERTY_CONFIG, oldValue, value, eventOrigin);
            this.setIgnorePropertyChanges(false);
            return this;
        }

        /**
         * Automatic:           Content has no Page set. PageTemplate is automatically solved.
         *                      return: null;
         *
         * ForcedTemplate:      Content has a Page set with at Page.template set
         *
         * ForcedController:    Content has a Page set with at Page.controller set
         *
         * NoController:        Content is:
         *                      1. PageTemplate with no controller set
         *                      2. Content that has no Page set and no any template that can be used as default
         *                      3. Content that has a Page set with at Page.template set but template was deleted
         *                         or updated to not support content's type
         */
        getPage(): Page {

            if (this.mode === PageMode.AUTOMATIC) {

                return null;
            } else if (this.mode === PageMode.FORCED_TEMPLATE) {

                let regionsUnchanged = this.defaultTemplate.getRegions().equals(this.regions);
                let regions = regionsUnchanged ? null : this.regions;

                let configUnchanged = this.defaultTemplate.getConfig().equals(this.config);
                let config = configUnchanged ? null : this.config;

                return new PageBuilder().
                    setTemplate(this.getTemplateKey()).
                    setRegions(regions).
                    setConfig(config).
                    setCustomized(this.isCustomized()).
                    setFragment(this.fragment).
                    build();
            } else if (this.mode === PageMode.FORCED_CONTROLLER) {
                return new PageBuilder().
                    setController(this.controller.getKey()).
                    setRegions(this.regions).
                    setConfig(this.config).
                    setCustomized(this.isCustomized()).
                    setFragment(this.fragment).
                    build();
            } else if (this.mode === PageMode.NO_CONTROLLER) {
                if (this.contentHasNonRenderableTemplateSet()) {
                    return new PageBuilder().
                        setTemplate(this.liveEditModel.getContent().getPage().getTemplate()).setFragment(this.fragment).
                        build();
                } else {
                    return null;
                }
            } else if (this.mode === PageMode.FRAGMENT) {
                return new PageBuilder().setRegions(null).setConfig(this.config).setCustomized(this.isCustomized()).setFragment(
                    this.fragment).build();
            } else {
                throw new Error('Page mode not supported: ' + this.mode);
            }

        }

        getDefaultPageTemplate(): PageTemplate {
            return this.defaultTemplate;
        }

        getDefaultPageDescriptor(): PageDescriptor {
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

        /**
         * Return page descriptor depending on page mode
         */
        getDescriptor(): PageDescriptor {
            let descriptor: PageDescriptor;

            if (!this.isPageTemplate()) {
                if (this.mode === PageMode.FORCED_TEMPLATE) {
                    return this.templateDescriptor;
                }

                if (this.mode === PageMode.AUTOMATIC) {
                    return this.defaultTemplateDescriptor;
                }
            }

            return this.controller;
        }

        getController(): PageDescriptor {
            return this.controller;
        }

        setControllerDescriptor(controller: PageDescriptor) {
            this.controller = controller;
        }

        hasTemplate(): boolean {
            return !!this.template;
        }

        hasDefaultPageTemplate(): boolean {
            return !!this.defaultTemplate;
        }

        getTemplateKey(): PageTemplateKey {
            return this.template ? this.template.getKey() : null;
        }

        getTemplate(): PageTemplate {
            return this.template;
        }

        getRegions(): api.content.page.region.Regions {
            return this.regions;
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        isCustomized(): boolean {
            return this.customized;
        }

        private contentHasNonRenderableTemplateSet() {
            return !this.isPageTemplate() && (this.mode === PageMode.NO_CONTROLLER) &&
                   this.liveEditModel.getContent().getPage() &&
                   this.liveEditModel.getContent().getPage().getTemplate();
        }

        private registerRegionsListeners(regions: api.content.page.region.Regions) {
            regions.onComponentPropertyChanged(this.componentPropertyChangedEventHandler);
            regions.onChanged(this.regionsChangedEventHandler);
        }

        private unregisterRegionsListeners(regions: api.content.page.region.Regions) {
            regions.unComponentPropertyChanged(this.componentPropertyChangedEventHandler);
            regions.unChanged(this.regionsChangedEventHandler);
        }

        private registerFragmentListeners(fragment: api.content.page.region.Component) {
            fragment.onPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        private unregisterFragmentListeners(fragment: api.content.page.region.Component) {
            fragment.unPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        onPageModeChanged(listener: (event: PageModeChangedEvent)=>void) {
            this.pageModeChangedListeners.push(listener);
        }

        unPageModeChanged(listener: (event: PageModeChangedEvent)=>void) {
            this.pageModeChangedListeners =
                this.pageModeChangedListeners.filter((curr: (event: PageModeChangedEvent)=>void) => {
                    return listener !== curr;
                });
        }

        private notifyPageModeChanged(oldValue: PageMode, newValue: PageMode) {
            let event = new PageModeChangedEvent(oldValue, newValue);
            this.pageModeChangedListeners.forEach((listener: (event: PageModeChangedEvent)=>void) => {
                listener(event);
            });
        }

        onPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners = this.propertyChangedListeners.filter((curr: (event: api.PropertyChangedEvent)=>void) => {
                return listener !== curr;
            });
        }

        private notifyPropertyChanged(property: string, oldValue: any, newValue: any, origin: any) {
            let event = new api.PropertyChangedEvent(property, oldValue, newValue, origin);
            this.propertyChangedListeners.forEach((listener: (event: api.PropertyChangedEvent)=>void) => {
                listener(event);
            });
        }

        onComponentPropertyChangedEvent(listener: (event: ComponentPropertyChangedEvent) => void) {
            this.componentPropertyChangedListeners.push(listener);
        }

        unComponentPropertyChangedEvent(listener: (event: ComponentPropertyChangedEvent) => void) {
            this.componentPropertyChangedListeners =
                this.componentPropertyChangedListeners.filter((curr: (event: ComponentPropertyChangedEvent) => void) => {
                    return listener !== curr;
                });
        }

        onCustomizeChanged(listener: (value: boolean) => void) {
            this.customizeChangedListeners.push(listener);
        }

        unCustomizeChanged(listener: (value: boolean) => void) {
            this.customizeChangedListeners = this.customizeChangedListeners.filter((curr: (value: boolean) => void) => {
                return listener !== curr;
            });
        }

        private notifyCustomizeChanged(value: boolean) {
            this.customizeChangedListeners.forEach((listener: (value: boolean)=>void) => {
                listener(value);
            });
        }

        onReset(listener: ()=>void) {
            this.resetListeners.push(listener);
        }

        unReset(listener: ()=>void) {
            this.resetListeners = this.resetListeners.filter((curr: ()=>void) => {
                return listener !== curr;
            });
        }

        private notifyReset() {
            this.resetListeners.forEach((listener: ()=>void) => {
                listener();
            });
        }

    }
}
