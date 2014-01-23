module app.contextwindow {
    export interface ContextWindowOptions {
        liveEditEl?:api.dom.IFrameEl;
        liveEditId?:string;
        site:api.content.Content;
    }

    export class ContextWindow extends api.ui.NavigableFloatingWindow {

        private site:api.content.Content;

        private componentTypesPanel:ComponentTypesPanel;
        private inspectorPanel:InspectorPanel;
        private emulatorPanel:EmulatorPanel;

        private draggingMask:api.ui.DraggingMask;
        private liveEditEl:api.dom.IFrameEl;
        private liveEditJQuery:JQueryStatic;
        private contextWindowOptions:ContextWindowOptions;
        private selectedComponent:Component;
        private minimizer:Minimizer;
        private pageRegions:api.content.page.PageRegions;

        constructor(options:ContextWindowOptions) {
            this.site = options.site;

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

        getSite():api.content.Content {
            return this.site;
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
            //TODO: Listen to component added event and generate component name. Set component name on component. Add component to region.
            //this.pageRegions.ensureUniqueComponentName()
            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('componentAdded.liveEdit', () => {
                console.log("component added!", arguments);
            });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('imageComponentSetImage.liveEdit', () => {
                console.log("image sat!", arguments);
                // TODO: var imageComponent = <api.content.page.image.ImageComponent>pageRegions.getComponent(event.getComponentName());
                // TODO: imageComponent.setImage(event.getImage());
                // TODO: liveFormPanel.saveChanges()
                //        -> setPage will be called automatically when saveChanges is finished
            });
        }

        setPage(content:api.content.Content, pageTemplate:api.content.page.PageTemplate) {
            var page = content.getPage();
            this.pageRegions = pageTemplate.getRegions();
            if (page.hasRegions()) {
                this.pageRegions = page.getRegions();
            }
        }

        getPageRegions() : api.content.page.PageRegions {
            return this.pageRegions;
        }

        getSelectedComponent():any {
            return this.selectedComponent;
        }

        getLiveEditJQuery():JQueryStatic {
            if (!this.liveEditJQuery) {
                //console.log(this.getLiveEditWindow());
                this.liveEditJQuery = <JQueryStatic>this.getLiveEditWindow().$liveEdit;
            }
            return this.liveEditJQuery;

        }

        getLiveEditEl():api.dom.IFrameEl {
            return this.liveEditEl;
        }

        getLiveEditWindow():any {
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

        private minimized:boolean;

        constructor(minimize:()=>void, maximize:()=>void, minimized:boolean = false) {
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