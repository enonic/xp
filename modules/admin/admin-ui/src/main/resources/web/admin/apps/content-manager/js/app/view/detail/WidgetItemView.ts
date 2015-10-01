module app.view.detail {

    import Element = api.dom.Element;
    import LabelEl = api.dom.LabelEl;

    export class WidgetItemView extends api.dom.DivEl {

        protected item: Element;

        constructor(className?: string) {
            super("widget-item-view" + (className ? " " + className : ""));
        }

        public setItem(item: Element) {
            this.item = item;
        }

        public layout() {
            if (this.item) {
                this.appendChild(this.item);
            }
        }

    }
}