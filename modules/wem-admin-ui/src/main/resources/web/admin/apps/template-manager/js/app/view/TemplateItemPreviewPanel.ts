module app.view {

    export class TemplateItemPreviewPanel extends api.app.view.ItemPreviewPanel {

        constructor() {
            super();
        }

        public setItem(item: api.app.view.ViewItem<app.browse.TemplateSummary>) {

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
