module app_contextwindow {
    export interface ContextWindowOptions {
        liveEditEl?:api_dom.Element;
        liveEditId?:string;
    }

    export class ContextWindow extends api_ui.NavigableFloatingWindow {
        private componentsPanel:ComponentsPanel;
        private inspectorPanel:api_ui.Panel;
        private emulatorPanel:api_ui.Panel;
        private draggingMask:DraggingMask;
        private liveEditEl:api_dom.Element;
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

            this.componentsPanel = new ComponentsPanel();
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
                var el = document.querySelector("#" + this.contextWindowOptions.liveEditId);
                if (el.tagName.toLowerCase() == "iframe") {
                    this.liveEditEl = api_dom.Element.fromHtmlElement(el);
                }
            }
            this.draggingMask = new DraggingMask(this.liveEditEl);
            document.body.appendChild(this.draggingMask.getHTMLElement());
        }

    }
}