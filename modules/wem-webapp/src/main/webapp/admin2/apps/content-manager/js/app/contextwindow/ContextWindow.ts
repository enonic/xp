module app_contextwindow {
    export interface ContextWindowOptions {
        liveEditEl?:api_dom.IFrameEl;
        liveEditId?:string;
    }

    export class ContextWindow extends api_ui.NavigableFloatingWindow {
        private componentsPanel:ComponentsPanel;
        private componentsPanelIndex:number;
        private inspectorPanel:InspectorPanel;
        private inspectorPanelIndex:number;
        private emulatorPanel:api_ui.Panel;
        private emulatorPanelIndex:number;

        private draggingMask:DraggingMask;
        private liveEditEl:api_dom.IFrameEl;
        private liveEditJQuery:JQueryStatic;
        private contextWindowOptions:ContextWindowOptions;
        private selectedComponent:any;

        constructor(options:ContextWindowOptions) {
            var dragStart = (event, ui) => {
                this.draggingMask.show();
            };

            var dragStop = (event, ui) => {
                this.draggingMask.hide();
            };

            super({draggableOptions: { start: dragStart, stop: dragStop } } );
            this.contextWindowOptions = options;
            this.addClass("context-window");

            this.componentsPanel = new ComponentsPanel(this);
            this.inspectorPanel = new InspectorPanel(this);
            this.emulatorPanel = new api_ui.Panel();

            this.componentsPanelIndex = this.addItem("Components", this.componentsPanel);
            this.inspectorPanelIndex = this.addItem("Inspector", this.inspectorPanel);
            this.emulatorPanelIndex = this.addItem("Emulator", this.emulatorPanel);

            ComponentSelectEvent.on((event) => {
                this.selectPanel(this.inspectorPanelIndex);
            });

            ComponentDeselectEvent.on((event) => {
                this.selectPanel(this.componentsPanelIndex);
            });

            if (options.liveEditEl) {
                this.liveEditEl = options.liveEditEl;
            }
        }

        afterRender() {
            if (this.contextWindowOptions.liveEditId) {
                var el = <HTMLIFrameElement>document.querySelector("#" + this.contextWindowOptions.liveEditId);
                if (el.tagName.toLowerCase() == "iframe") {
                    this.liveEditEl = <api_dom.IFrameEl> api_dom.IFrameEl.fromHtmlElement(el);
                }
            }
            this.draggingMask = new DraggingMask(this.liveEditEl);
            document.body.appendChild(this.draggingMask.getHTMLElement());
            this.liveEditListen();
        }

        private liveEditListen() {
            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('selectComponent.liveEdit', (event, component, mouseClickPagePosition) => {
                new ComponentSelectEvent(<Component>component).fire();
                this.selectedComponent = component;
            });

            this.getLiveEditJQuery()(this.getLiveEditWindow()).on('deselectComponent.liveEdit', (event) => {
                new ComponentDeselectEvent().fire();
                this.selectedComponent = null;
            });
        }

        getSelectedComponent():any {
            return this.selectedComponent;
        }

        getLiveEditJQuery():JQueryStatic {
            if (!this.liveEditJQuery) {
                console.log(this.getLiveEditWindow());
                this.liveEditJQuery = <JQueryStatic>this.getLiveEditWindow().$liveEdit;
            }
            return this.liveEditJQuery;

        }

        getLiveEditWindow():any {
            //TODO: "contentwindow" is hacky because we need HTMLIFrameElement to fetch that property, but it is impossible to cast to ><
            return this.liveEditEl.getHTMLElement()["contentWindow"];
        }

    }
}