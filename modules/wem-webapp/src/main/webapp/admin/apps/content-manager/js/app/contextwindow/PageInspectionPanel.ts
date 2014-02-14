module app.contextwindow {

    export class PageInspectionPanel extends BaseInspectionPanel {

        private page: api.content.Content;

        constructor() {
            super("live-edit-font-icon-page");
        }

        setPage(page: api.content.Content) {
            this.page = page;
            if (page) {
                this.setMainName(page.getDisplayName());
                this.setSubName(page.getPath().toString());
            } else {
                this.setMainName("[No Page given]");
                this.setSubName("");
            }
        }

    }
}