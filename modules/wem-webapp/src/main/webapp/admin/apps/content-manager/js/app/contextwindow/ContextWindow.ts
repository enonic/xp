module app.contextwindow {

    import ComponentPath = api.content.page.ComponentPath;
    import ImageComponent = api.content.page.image.ImageComponent;
    import TemplateKey = api.content.page.TemplateKey;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;

    export interface ContextWindowConfig {
        liveEditIFrame?:api.dom.IFrameEl;
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
        private liveEditIFrame: api.dom.IFrameEl;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;
        private selectedComponent: Component;
        private minimizer: Minimizer;
        private pageRegions: api.content.page.PageRegions;
        private contentSaveAction: api.ui.Action;

        constructor(config: ContextWindowConfig) {

            this.siteTemplate = config.siteTemplate;
            this.contentSaveAction = config.contentSaveAction;
            this.liveEditIFrame = config.liveEditIFrame;
            //TODO: "contentwindow" is hacky because we need HTMLIFrameElement to fetch that property, but it is impossible to cast to ><
            this.liveEditWindow = this.liveEditIFrame.getHTMLElement()["contentWindow"];
            this.liveEditJQuery = <JQueryStatic>this.liveEditWindow.$liveEdit;

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

            this.addClass("context-window");

            this.draggingMask = new api.ui.DraggingMask(this.liveEditIFrame);

            this.componentTypesPanel = new ComponentTypesPanel({
                contextWindow: this,
                liveEditIFrame: this.liveEditIFrame,
                liveEditWindow: this.liveEditWindow,
                liveEditJQuery: this.liveEditJQuery,
                draggingMask: this.draggingMask
            });

            this.inspectorPanel = new InspectorPanel({
                liveEditWindow: this.liveEditWindow,
                siteTemplate: this.siteTemplate
            });
            this.emulatorPanel = new EmulatorPanel({
                liveEditIFrame: this.liveEditIFrame
            });

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


            document.body.appendChild(this.draggingMask.getHTMLElement());
            this.liveEditListen();
        }

        private liveEditListen() {
            this.liveEditJQuery(this.liveEditWindow).on('selectComponent.liveEdit',
                (event, component?, mouseClickPagePosition?) => {
                    new SelectComponentEvent(<Component>component).fire();
                    this.selectedComponent = component;
                });

            this.liveEditJQuery(this.liveEditWindow).on('componentSelect.liveEdit',
                (event, name?) => {
                    new ComponentSelectEvent(new api.content.page.ComponentName(name)).fire();
                });

            this.liveEditJQuery(this.liveEditWindow).on('pageSelect.liveEdit',
                (event) => {
                    new PageSelectEvent().fire();
                });

            this.liveEditJQuery(this.liveEditWindow).on('regionSelect.liveEdit',
                (event, name?) => {
                    new RegionSelectEvent(name).fire();
                });

            this.liveEditJQuery(this.liveEditWindow).on('deselectComponent.liveEdit', (event) => {
                new ComponentDeselectEvent().fire();
                this.selectedComponent = null;
            });

            this.liveEditJQuery(this.liveEditWindow).on('componentRemoved.liveEdit', (event) => {
                new ComponentRemovedEvent().fire();
                this.selectedComponent = null;
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableStop.liveEdit', (event) => {
                new LiveEditDragStopEvent().fire();
                this.show();
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableStart.liveEdit', (event) => {
                new LiveEditDragStartEvent().fire();
                this.hide();
            });
            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit', (event, component?) => {
                console.log("recieved sortable update", arguments);
                var componentPath = api.content.page.ComponentPath.fromString(component.getComponentPath());
                var afterComponentPath = api.content.page.ComponentPath.fromString(component.getPrecedingComponentPath());
                console.log(this.pageRegions.moveComponent(componentPath, component.getRegionName(), afterComponentPath));

            });


            this.liveEditJQuery(this.liveEditWindow).on('componentAdded.liveEdit',
                (event, component?, regionName?, componentPathToAddAfterAsString?: string) => {
                    //TODO: Make all components work and not only image

                    var componentName = this.pageRegions.ensureUniqueComponentName(new api.content.page.ComponentName("Image"));

                    var componentToAddAfter: api.content.page.ComponentPath = null;
                    if (componentPathToAddAfterAsString) {
                        componentToAddAfter = api.content.page.ComponentPath.fromString(componentPathToAddAfterAsString);
                    }

                    var regionPath: api.content.page.RegionPath;
                    if (componentToAddAfter != null) {
                        regionPath = componentToAddAfter.getRegionPath();
                    }
                    else {
                        regionPath = api.content.page.RegionPath.fromString(regionName);
                    }

                    var imageComponent = new ImageComponentBuilder();
                    imageComponent.setName(componentName);
                    imageComponent.setRegion(regionPath);

                    var componentPath;
                    if (componentToAddAfter) {
                        componentPath = this.pageRegions.addComponentAfter(imageComponent.build(), componentToAddAfter);
                    }
                    else {
                        this.pageRegions.addComponentFirst(imageComponent.build(), regionName);
                        componentPath = ComponentPath.fromString(regionName + "/" + componentName.toString());
                    }

                    component.getEl().setData("live-edit-component", componentPath.toString());
                    component.getEl().setData("live-edit-name", componentName.toString());
                });

            this.liveEditJQuery(this.liveEditWindow).on('imageComponentSetImage.liveEdit',
                (event, imageId?, componentPathAsString?) => {

                    var componentPath = ComponentPath.fromString(componentPathAsString);
                    var defaultImageTemplate = this.siteTemplate.getDefaultImageTemplate();

                    var imageComponent = this.pageRegions.getImageComponent(componentPath);
                    if (imageComponent != null) {
                        imageComponent.setImage(imageId);
                        imageComponent.setTemplate(defaultImageTemplate);
                        this.contentSaveAction.execute();
                    }
                    else {
                        console.log("ImageComponent to set image on not found: " + componentPath);
                    }
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

        private minimize() {
            this.getDeck().hide();
            this.getEl().addClass("minimized");
        }

        private maximize() {
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