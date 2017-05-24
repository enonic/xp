import Panel = api.ui.panel.Panel;
import {IssueType} from '../IssueType';
import {IssueList, IssueListItem} from './IssueList';
export class IssuesPanel extends Panel {

    private issuesType: IssueType;

    private issuesList: IssueList;

    private issueSelectedListeners: {(id: IssueListItem): void}[] = [];

    constructor(issuesType: IssueType) {
        super(IssueType[issuesType]);

        this.issuesType = issuesType;
        this.issuesList = new IssueList(this.issuesType);

        this.issuesList.onIssueSelected((issueListItem) => {
            this.notifyIssueSelected(issueListItem);
        });

        this.appendChild(this.issuesList);
    }

    public getIssuesList(): IssueList {
        return this.issuesList;
    }

    public reload(): wemQ.Promise<void> {
        return this.issuesList.reload();
    }

    public refresh() {
        this.issuesList.refreshList();
    }

    public onIssueSelected(listener: (id: IssueListItem) => void) {
        this.issueSelectedListeners.push(listener);
    }

    public unIssueSelected(listener: (id: IssueListItem) => void) {
        this.issueSelectedListeners = this.issueSelectedListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyIssueSelected(issueListItem: IssueListItem) {
        this.issueSelectedListeners.forEach(listener => {
            listener(issueListItem);
        });
    }
}
