module app.contextwindow {

    import ComponentPath = api.content.page.ComponentPath;
    import ImageComponent = api.content.page.image.ImageComponent;
    import TemplateKey = api.content.page.TemplateKey;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;

    export interface ContextWindowOptions {
        liveEditEl?:api.dom.IFrameEl;
        liveEditId?:string;
        siteTemplate:api.content.site.template.SiteTemplate;
        contentSaveAction: api.ui.Action;
    }

    export class ContextWindow extends api.ui.NavigableFloatingWindow {

        private siteTemplate: api.content.site.template.SiteTemplate;

        private componentTypesPanel: ComponentTypesPanel;
        private inspectorPanel: InspectorPanel;
        private emulatorPanel: EmulatorPanel;

        private draggingMask: api.ui.DraggingMask;
        private liveEditEl: api.dom.IFrameEl;
        private liveEditJQuery: JQueryStatic;
        private contextWindowOptions: ContextWindowOptions;
        private selectedComponent: Component;
        private minimizer: Minimizer;
        private pageRegions: api.content.page.PageRegions;
        private contentSaveAction: api.ui.Action;

        constructor(options: ContextWindowOptions) {

            this.siteTemplate = options.siteTemplate;
            this.contentSaveAction = options.contentSaveAction;
            var dragStart = (event, ui) => {
                this.draggingMask.show();
            };

            var dragStop = (event, ui) => {
                this.draggingMask.hide();
            };

            super({draggableOptions: {
                start: dragStart,
                stop: dragStop,
                handle: ".tab-bar"
            } });
            this.contextWindowOptions = options;
            this.addClass("context-window");

            this.componentTypesPanel = new ComponentTypesPanel(this);
            this.inspectorPanel = new InspectorPanel(this);
            this.emulatorPanel = new EmulatorPanel(this);

            this.addItem("Insert", this.componentTypesPanel);
            this.addItem("Settings", this.inspectorPanel);
            this.addItem("Emulator", this.emulatorPanel);

            this.minimizer = new Minimizer(()=> {
                this.minimize();
            }, ()=> {
                this.maximize();
            });
            this.getNavigator().appendChild(this.minimizer);

            SelectComponentEvent.on((event) => {
                this.selectPanel(this.inspectorPanel);
                this.selectedComponent = event.getComponent();
            });

            ComponentDeselectEvent.on((event) => {
                this.selectPanel(this.componentTypesPanel);
            });

            ComponentRemovedEvent.on((event) => {
                this.selectPanel(this.componentTypesPanel);
            });

            if (options.liveEditEl) {
                this.liveEditEl = options.liveEditEl;
            }
        }

        afterRender() {
            if (this.contextWindowOptions.liveEditId) {
                var el = <HTMLIFrameElement>document.querySelector("#" + this.contextWindowOptions.liveEditId);
                if (el.tagName.toLowerCase() == "iframe") {
                    this.liveEditEl = <api.dom.IFrameEl> api.dom.IFrameEl.fromHtmlElement(el);
                }
            }
            this.draggingMask = new api.ui.DraggingMask(this.liveEditEl);
            document.body.appendChild(this.draggingMask.getHTMLElement());
            this.liveEditListen();
        }

        getDraggingMask() {
            return this.draggingMask;
        }

        private liveEditListen() {
            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('selectComponent.liveEdit',
                (event, component?, mouseClickPagePosition?) => {
                    new SelectComponentEvent(<Component>component).fire();
                    this.selectedComponent = component;
                });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('componentSelect.liveEdit',
                (event, name?) => {
                    new ComponentSelectEvent(new api.content.page.ComponentName(name)).fire();
                });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('pageSelect.liveEdit',
                (event) => {
                    new PageSelectEvent().fire();
                });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('regionSelect.liveEdit',
                (event, name?) => {
                    new RegionSelectEvent(name).fire();
                });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('deselectComponent.liveEdit', (event) => {
                new ComponentDeselectEvent().fire();
                this.selectedComponent = null;
            });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('componentRemoved.liveEdit', (event) => {
                new ComponentRemovedEvent().fire();
                this.selectedComponent = null;
            });
            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('sortableStop.liveEdit', (event) => {
                new LiveEditDragStopEvent().fire()
                this.show();
            });
            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('sortableStart.liveEdit', (event) => {
                new LiveEditDragStartEvent().fire()
                this.hide();
            });


            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('componentAdded.liveEdit', (event, component?, regionName?) => {

                //TODO: Make all components work and not only image
                var componentName = this.pageRegions.ensureUniqueComponentName(new api.content.page.ComponentName("Image"));
                var componentPath = ComponentPath.fromString(regionName + "/" + componentName.toString());
                component.getEl().setData("live-edit-component", componentPath.toString());
                component.getEl().setData("live-edit-name", componentName.toString());

                var imageComponent = new ImageComponentBuilder();
                imageComponent.setName(componentName);
                this.pageRegions.addComponent(imageComponent.build(), regionName);
            });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('imageComponentSetImage.liveEdit',
                (event, imageId?, componentPathAsString?) => {

                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    var defaultImageTemplate = this.siteTemplate.getDefaultImageTemplate();

                    var imageComponent = this.pageRegions.getImageComponent(componentPath);
                    imageComponent.setImage(imageId);
                    imageComponent.setTemplate(defaultImageTemplate);
                    this.contentSaveAction.execute();
                });
        }

        setPage(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {
            var page = content.getPage();
            this.pageRegions = pageTemplate.getRegions();
            if (page.hasRegions()) {
                this.pageRegions = page.getRegions();
            }
        }

        getPageRegions(): api.content.page.PageRegions {
            return this.pageRegions;
        }

        getSelectedComponent(): any {
            return this.selectedComponent;
        }

        getLiveEditJQuery(): JQueryStatic {
            if (!this.liveEditJQuery) {
                //console.log(this.getLiveEditWindow());
                this.liveEditJQuery = <JQueryStatic>this.getLiveEditWindow().$liveEdit;
            }
            return this.liveEditJQuery;

        }

        getLiveEditEl(): api.dom.IFrameEl {
            return this.liveEditEl;
        }

        getLiveEditWindow(): any {
            //TODO: "contentwindow" is hacky because we need HTMLIFrameElement to fetch that property, but it is impossible to cast to ><
            return this.liveEditEl.getHTMLElement()["contentWindow"];
        }

        minimize() {
            this.getDeck().hide();
            this.getEl().addClass("minimized");
        }

        maximize() {
            this.getDeck().show();
            this.getEl().removeClass("minimized");
        }

    }

    class Minimizer extends api.dom.DivEl {

        private minimized: boolean;

        constructor(minimize: ()=>void, maximize: ()=>void, minimized: boolean = false) {
            super("minimizer live-edit-font-icon-minimize");
            this.minimized = minimized;

            this.getEl().addEventListener("click", (event) => {
                if (this.minimized) {
                    this.removeClass("live-edit-font-icon-maximize");
                    this.addClass("live-edit-font-icon-minimize");
                    maximize();
                    this.minimized = false;
                } else {
                    this.removeClass("live-edit-font-icon-minimize");
                    this.addClass("live-edit-font-icon-maximize");
                    minimize();
                    this.minimized = true;
                }
            });
        }


    }
}