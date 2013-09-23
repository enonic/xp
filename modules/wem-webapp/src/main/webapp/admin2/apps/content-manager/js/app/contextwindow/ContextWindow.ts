module app_contextwindow {
    export interface ContextWindowOptions {
        liveEditEl?:api_dom.IFrameEl;
        liveEditId?:string;
    }

    export class ContextWindow extends api_ui.NavigableFloatingWindow {
        private componentsPanel:ComponentsPanel;
        private inspectorPanel:api_ui.Panel;
        private emulatorPanel:api_ui.Panel;
        private draggingMask:DraggingMask;
        private liveEditEl:api_dom.IFrameEl;
        private liveEditJQuery:JQueryStatic;
        private contextWindowOptions:ContextWindowOptions;


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
            this.inspectorPanel = new api_ui.Panel();
            this.emulatorPanel = new api_ui.Panel();

            this.addItem("Components", this.componentsPanel);
            this.addItem("Inspector", this.inspectorPanel);
            this.addItem("Emulator", this.emulatorPanel);

            if (options.liveEditEl) {
                this.liveEditEl = options.liveEditEl;
            }
        }

        afterRender() {
            if (this.contextWindowOptions.liveEditId) {
                var el = <HTMLIFrameElement>document.querySelector("#" + this.contextWindowOptions.liveEditId);
                if (el.tagName.toLowerCase() == "iframe") {
                    this.liveEditEl = api_dom.IFrameEl.fromHtmlElement(el);
                }
            }
            this.draggingMask = new DraggingMask(this.liveEditEl);
            document.body.appendChild(this.draggingMask.getHTMLElement());
        }

        getLiveEditJQuery():JQueryStatic {
            if (!this.liveEditJQuery) {
                //TODO: "contentwindow" is hacky because we need HTMLIFrameElement to fetch that property, but it is impossible to cast to ><
                this.liveEditJQuery = <JQueryStatic>this.liveEditEl.getHTMLElement()["contentWindow"].$liveEdit;
            }
            return this.liveEditJQuery;

        }

    }
}