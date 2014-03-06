module app.contextwindow.inspect {

    export class ContentInspectionPanel extends BaseInspectionPanel {

        private content: api.content.Content;

        constructor() {
            super("live-edit-font-icon-content");
        }

        setContent(content: api.content.Content) {
            this.content = content;
            if (content) {
                this.setMainName(content.getDisplayName());
                this.setSubName(content.getPath().toString());
            } else {
                this.setMainName("[No Content given]");
                this.setSubName("");
            }
        }

    }
}