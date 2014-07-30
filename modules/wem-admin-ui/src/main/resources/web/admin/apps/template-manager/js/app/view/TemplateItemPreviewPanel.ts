module app.view {

    export class TemplateItemPreviewPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        private mask: api.ui.LoadMask;

        constructor() {
            super("item-preview-panel");
            this.mask = new api.ui.LoadMask(this);
            this.frame = new api.dom.IFrameEl();
            this.frame.onLoaded((event: UIEvent) => this.mask.hide());
            this.appendChild(this.frame);
        }

        public setItem(item: api.app.view.ViewItem<api.content.TemplateSummary>) {

            var templateSummary = item.getModel();
            if (templateSummary.isPageTemplate()) {
                this.show();
                this.mask.show();

                templateSummary.getKey();
                var url = api.util.getUri('portal/theme/' + templateSummary.getSiteTemplateKey().toString() + '/' +
                                          templateSummary.getKey());
                this.getEl().removeClass("no-preview").addClass('page-preview');
                this.frame.setSrc(url);
            } else {
                this.hide();
            }
        }

    }
}
