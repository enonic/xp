module app.contextwindow {

    export class PageInspectionPanel extends BaseInspectionPanel {

        private page: api.content.Content;

        constructor() {
            super("live-edit-font-icon-page");
        }

        setPage(page: api.content.Content) {
            this.page = page;
            if (page) {
                this.setName(page.getDisplayName(), page.getPath().toString());
            } else {
                this.setName('[No Name]', 'page');
            }
        }

    }
}