module app.contextwindow {

    export class ContentInspectionPanel extends BaseInspectionPanel {

        private content: api.content.Content;

        constructor() {
            super("live-edit-font-icon-content");
        }

        setContent(content: api.content.Content) {
            this.content = content;
            if (content) {
                this.setName(content.getDisplayName(), content.getPath().toString());
            } else {
                this.setName('[No Name]', 'content');
            }
        }

    }
}