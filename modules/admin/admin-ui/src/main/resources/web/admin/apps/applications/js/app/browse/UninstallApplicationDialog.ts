import "../../api.ts";
import {UninstallApplicationEvent} from "./UninstallApplicationEvent";
import Application = api.application.Application;
import Action = api.ui.Action;

export class UninstallApplicationDialog extends api.ui.dialog.ModalDialog {

    private applications: Application[];

    private yesAction: Action = new Action('Yes');

    private noAction: Action = new Action('No');

    constructor(applications: Application[]) {
        super('Uninstall Applications');

        this.applications = applications;
        this.addClass('uninstall-dialog');

        let message = new api.dom.H6El();
        message.getEl().setInnerHtml('Are you sure you want to uninstall selected application(s)?');
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
