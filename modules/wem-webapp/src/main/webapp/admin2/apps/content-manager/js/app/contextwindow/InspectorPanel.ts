module app_contextwindow {
    export class InspectorPanel extends api_ui.Panel {
        private header:api_dom.H3El;
        private subtitle:api_dom.Element;
        private iconEl:api_dom.DivEl;
        private infoEl:api_dom.Element;

        constructor(contextWindow:ContextWindow) {
            super("InspectorPanel");
            this.addClass("inspector-panel");

            this.initElements();
            this.setEmpty();

            ComponentSelectEvent.on((event) => {
                this.setName(event.getComponent().name);
                this.setType(event.getComponent().componentType.typeName);
                this.setIcon(event.getComponent().componentType.iconCls);

            });

            ComponentDeselectEvent.on((event) => {
                this.setEmpty();
            });

        }

        private initElements() {
            this.header = new api_dom.H3El();
            this.subtitle = new api_dom.DivEl();
            this.iconEl = new api_dom.DivEl();
            this.infoEl = new api_dom.DivEl();

            this.iconEl.addClass("icon");

            this.appendChild(this.iconEl);
            this.appendChild(this.header);
            this.appendChild(this.subtitle);
            this.appendChild(this.infoEl);
        }

        private setEmpty() {
            this.header.getEl().setInnerHtml("Empty");
            this.subtitle.getEl().setInnerHtml("No component selected");
            this.iconEl.removeAllClasses("icon");
        }

        setIcon(iconCls:string) {
            this.iconEl.addClass(iconCls);
        }

        setName(name:string) {
            this.subtitle.getEl().setInnerHtml(name);
        }

        setType(type:string) {
            this.header.getEl().setInnerHtml(type);
        }
    }
}