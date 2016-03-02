module app.browse {

    export class UninstallApplicationDialog extends api.ui.dialog.ModalDialog {

        private applications: Application[];

        private yesAction = new api.ui.Action('Yes');

        private noAction = new api.ui.Action('No');


        constructor(applications: Application[]) {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Uninstall Applications")
            });
            this.applications = applications;
            this.addClass("uninstall-dialog");

            var message = new api.dom.H6El();
            message.getEl().setInnerHtml("Are you sure you want to uninstall selected application(s)?");
            this.appendChildToContentPanel(message);

            this.yesAction.onExecuted(() => {
                new UninstallApplicationEvent(this.applications).fire();
                this.close();
            });
            this.addAction(this.yesAction);

            this.noAction.onExecuted(() => {
                this.close();
            });
            this.addAction(this.noAction);
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }
    }
}