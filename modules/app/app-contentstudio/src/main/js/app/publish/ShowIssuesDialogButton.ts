import '../../api.ts';
import {ShowIssuesDialogAction} from '../browse/action/ShowIssuesDialogAction';
import {IssueFetcher} from './IssueFetcher';
import {IssueStatsJson} from './IssueStatsJson';
import ActionButton = api.ui.button.ActionButton;
import IssueServerEventsHandler = api.issue.event.IssueServerEventsHandler;

export class ShowIssuesDialogButton extends ActionButton {

    constructor() {
        super(new ShowIssuesDialogAction());

        this.addClass('show-issues-dialog-button');

        this.updateShowIssuesDialogButton();

        this.initEventsListeners();
    }

    private initEventsListeners() {
        IssueServerEventsHandler.getInstance().onIssueCreated(() => {
            setTimeout(() => {  // giving a chance for backend to refresh indexes so we get correct stats
                this.updateShowIssuesDialogButton();
            }, 1000)
        });

        IssueServerEventsHandler.getInstance().onIssueUpdated(() => {
            setTimeout(() => { // giving a chance for backend to refresh indexes so we get correct stats
                this.updateShowIssuesDialogButton();
            }, 1000)
        });
    }

    private updateShowIssuesDialogButton() {
        IssueFetcher.fetchIssueStats().then((stats: IssueStatsJson) => {
            this.toggleClass('has-assigned-issues', stats.assignedToMe > 0);
            this.getEl().setTitle((stats.assignedToMe == 0) ?
                                  'Publishing Issues' :
                                  'You have unclosed Publishing Issues');
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });
    }
}
