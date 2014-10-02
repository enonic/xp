module app.view {

    import Workspace = api.content.Workspace;

    export class TemplateItemPreviewPanel extends api.app.view.ItemPreviewPanel {

        constructor() {
            super();
        }

        public setItem(item: api.app.view.ViewItem<app.browse.TemplateSummary>) {

            var templateSummary = item.getModel();
            if (templateSummary.isPageTemplate()) {
                this.show();
                this.mask.show();

                var workspace = Workspace[Workspace.STAGE].toLowerCase();
                var siteTemplateKey = templateSummary.getSiteTemplateKey().toString();
                var pageTemplateKey = templateSummary.getKey();
                var url = api.util.UriHelper.getUri('portal/' + workspace + '/page-template/' + siteTemplateKey + '/' + pageTemplateKey);
                this.getEl().removeClass("no-preview").addClass('page-preview');
                this.frame.setSrc(url);
            } else {
                this.hide();
            }
        }

    }
}
