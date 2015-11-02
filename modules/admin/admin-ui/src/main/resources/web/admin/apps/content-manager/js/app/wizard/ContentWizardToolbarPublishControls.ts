module app.wizard {

    import Action = api.ui.Action;
    import DialogButton = api.ui.dialog.DialogButton;
    import SpanEl = api.dom.SpanEl;
    import CompareStatus = api.content.CompareStatus;

    export class ContentWizardToolbarPublishControls extends api.dom.DivEl {

        private publishButton: DialogButton;
        private contentStateSpan: SpanEl;
        private publishAction: Action;
        private contentCanBePublished: boolean = false;
        private userCanPublish: boolean = true;
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

        public setCompareStatus(compareStatus: CompareStatus, refresh: boolean = true) {
            this.contentCompareStatus = compareStatus;
            if (refresh) {
                this.refreshState();
            }
        }

        public setContentCanBePublished(value: boolean, refresh: boolean = true) {
            this.contentCanBePublished = value;
            if (refresh) {
                this.refreshState();
            }
        }

        public setUserCanPublish(value: boolean, refresh: boolean = true) {
            this.userCanPublish = value;
            if (refresh) {
                this.refreshState();
            }
        }

        public refreshState() {
            var canBeEnabled = this.contentCompareStatus !== CompareStatus.EQUAL && this.contentCanBePublished && this.userCanPublish;
            this.publishAction.setEnabled(canBeEnabled);
            this.contentStateSpan.setHtml(this.getContentStateValueForSpan(this.contentCompareStatus));
        }

        public isOnline(): boolean {
            return this.contentCompareStatus === CompareStatus.EQUAL;
        }

        public isPendingDelete(): boolean {
            return this.contentCompareStatus == CompareStatus.PENDING_DELETE;
        }

        public enableActionsForExisting(existing: api.content.Content) {
            new api.security.auth.IsAuthenticatedRequest().
                sendAndParse().
                then((loginResult: api.security.auth.LoginResult) => {
                    var hasPublishPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
                        loginResult, existing.getPermissions());
                    this.setUserCanPublish(hasPublishPermission);
                });
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