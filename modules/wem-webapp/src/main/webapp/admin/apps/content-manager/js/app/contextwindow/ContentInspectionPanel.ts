module app.contextwindow {

    export class ContentInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private content: api.content.Content;

        constructor(config: ComponentInspectionPanelConfig) {
            super(config, "live-edit-font-icon-content");

        }

        setContent(content: api.content.Content) {
            this.content = content;
            if (content) {
                this.setName(content.getDisplayName(), 'content');
            } else {
                this.setName('[No Name]', 'content');
            }

        }

    }
}