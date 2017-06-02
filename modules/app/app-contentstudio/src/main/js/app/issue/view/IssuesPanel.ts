import Panel = api.ui.panel.Panel;
import {IssueList} from './IssueList';
import {IssueStatus} from '../IssueStatus';

export class IssuesPanel extends Panel {

    private issuesList: IssueList;

    constructor(issueStatus: IssueStatus) {
        super(IssueStatus[issueStatus]);

        this.issuesList = new IssueList(issueStatus);
        this.appendChild(this.issuesList);
    }

    public getItemCount(): number {
        return this.issuesList.getItemCount();
    }

    public getTotalItems(): number {
        return this.issuesList.getTotalItems();
    }

    public reload(): wemQ.Promise<void> {
        return this.issuesList.reload();
    }
}
