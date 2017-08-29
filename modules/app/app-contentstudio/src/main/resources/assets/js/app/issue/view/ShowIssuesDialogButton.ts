import ActionButton = api.ui.button.ActionButton;
import {ShowIssuesDialogAction} from '../../browse/action/ShowIssuesDialogAction';
import {IssueServerEventsHandler} from '../event/IssueServerEventsHandler';
import {IssueResponse} from '../resource/IssueResponse';
import {ListIssuesRequest} from '../resource/ListIssuesRequest';
import {IssueStatus} from '../IssueStatus';
import i18n = api.util.i18n;

export class ShowIssuesDialogButton extends ActionButton {

    constructor() {
        super(new ShowIssuesDialogAction());

        this.addClass('show-issues-dialog-button');

        this.updateShowIssuesDialogButton();

        this.initEventsListeners();
    }

    private initEventsListeners() {
        IssueServerEventsHandler.getInstance().onIssueCreated(() => {
            this.updateShowIssuesDialogButton();
        });

        IssueServerEventsHandler.getInstance().onIssueUpdated(() => {
            this.updateShowIssuesDialogButton();
        });
    }

    private updateShowIssuesDialogButton() {
        new ListIssuesRequest().setAssignedToMe(true).setIssueStatus(IssueStatus.OPEN).setSize(0).sendAndParse().then(
            (response: IssueResponse) => {
                this.toggleClass('has-assigned-issues', response.getMetadata().getTotalHits() > 0);
                this.getEl().setTitle((response.getMetadata().getTotalHits() === 0) ?
                                      i18n('text.publishingissues') :
                                      i18n('text.youhaveissues'));
            }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });
    }
}
