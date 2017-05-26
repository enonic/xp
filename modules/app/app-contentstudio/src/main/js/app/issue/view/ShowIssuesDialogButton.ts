
import ActionButton = api.ui.button.ActionButton;
import {ShowIssuesDialogAction} from '../../browse/action/ShowIssuesDialogAction';
import {IssueFetcher} from '../IssueFetcher';
import {IssueServerEventsHandler} from '../event/IssueServerEventsHandler';
import {IssueType} from '../IssueType';
import {IssueResponse} from '../resource/IssueResponse';

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
        IssueFetcher.fetchIssuesByType(IssueType.ASSIGNED_TO_ME, 0, 1).then((response: IssueResponse) => {
            this.toggleClass('has-assigned-issues', response.getMetadata().getTotalHits() > 0);
            this.getEl().setTitle((response.getMetadata().getTotalHits() === 0) ?
                                  'Publishing Issues' :
                                  'You have unclosed Publishing Issues');
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });
    }
}
