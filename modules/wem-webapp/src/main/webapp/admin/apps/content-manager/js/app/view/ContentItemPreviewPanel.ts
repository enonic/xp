module app.view {

    export class ContentItemPreviewPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        constructor() {
            super("item-preview-panel");
            this.frame = new api.dom.IFrameEl();
            this.appendChild(this.frame);
        }

        public setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            var escapedPath = item.getPath();
            if (escapedPath.charAt(0) == '/') {
                escapedPath = escapedPath.substring(1);
            }
            this.frame.setSrc(api.util.getUri("portal/live/" + escapedPath));
        }

    }
}
