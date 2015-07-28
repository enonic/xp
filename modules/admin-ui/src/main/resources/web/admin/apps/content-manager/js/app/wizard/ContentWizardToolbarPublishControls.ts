module app.wizard {

    import Action = api.ui.Action;
    import DialogButton = api.ui.dialog.DialogButton;
    import SpanEl = api.dom.SpanEl;
    import CompareStatus = api.content.CompareStatus;

    export class ContentWizardToolbarPublishControls extends api.dom.DivEl {

        private publishButton: DialogButton;
        private contentStateSpan: SpanEl;
        private publishAction: Action;
        private contentCompareStatus: CompareStatus;

        constructor(action: Action) {
            super("toolbar-publish-controls");

            this.publishAction = action;
            this.publishAction.setIconClass("publish-action");

            this.publishButton = new DialogButton(action);
            this.publishButton.addClass("content-wizard-toolbar-publish-button");

            this.contentStateSpan = new SpanEl("content-status");

            this.appendChildren(this.contentStateSpan, this.publishButton);
        }

        public setCompareStatus(compareStatus: CompareStatus) {
            var canBeEnabled = compareStatus !== CompareStatus.EQUAL;
            this.publishAction.setEnabled(canBeEnabled);
            this.contentStateSpan.setHtml(this.getContentStateValueForSpan(compareStatus));
        }

        private getContentStateValueForSpan(compareStatus: CompareStatus): string {
            var status = new api.dom.SpanEl();
            if (compareStatus === CompareStatus.EQUAL) {
                status.addClass("online");
            }
            status.setHtml(api.content.CompareStatusFormatter.formatStatus(compareStatus));
            return "Item is " + status.toString();
        }
    }
}