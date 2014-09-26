module api.app.view {

    export class ItemPreviewPanel extends api.ui.panel.Panel {

        public frame: api.dom.IFrameEl;

        public mask: api.ui.mask.LoadMask;

        constructor(className?: string) {
            super("item-preview-panel" + (className ? " " + className : ""));
            this.mask = new api.ui.mask.LoadMask(this);
            this.frame = new api.dom.IFrameEl();
            this.frame.onLoaded((event: UIEvent) => this.mask.hide());
            this.appendChild(this.frame);
        }

    }
}
