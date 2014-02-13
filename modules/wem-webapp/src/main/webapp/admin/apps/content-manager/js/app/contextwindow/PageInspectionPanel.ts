module app.contextwindow {

    export class PageInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private page: api.content.Content;

        constructor(config: ComponentInspectionPanelConfig) {
            super(config, "live-edit-font-icon-page");

        }

        setPage(page: api.content.Content) {
            this.page = page;
            if (page) {
                this.setName(page.getDisplayName(), 'page');
            } else {
                this.setName('[No Name]', 'page');
            }
        }

    }
}