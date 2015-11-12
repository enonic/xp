module app.view.detail {

    import Element = api.dom.Element;
    import LabelEl = api.dom.LabelEl;

    export class WidgetItemView extends api.dom.DivEl {

        public static debug = false;

        constructor(className?: string) {
            super("widget-item-view" + (className ? " " + className : ""));
        }

        public layout(): wemQ.Promise<any> {
            if (WidgetItemView.debug) {
                console.debug('WidgetItemView.layout: ', this);
            }
            return wemQ<any>(null);
        }

    }
}