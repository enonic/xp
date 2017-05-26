import Panel = api.ui.panel.Panel;
import {IssueType} from '../IssueType';
import {IssueList} from './IssueList';
export class IssuesPanel extends Panel {

    private issuesList: IssueList;

    constructor(issuesType: IssueType) {
        super(IssueType[issuesType]);

        this.issuesList = new IssueList(issuesType);
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
