import '../../api.ts';
import {IssueSummary} from './IssueSummary';
import ListBox = api.ui.selector.list.ListBox;
import DateHelper = api.util.DateHelper;
import NamesView = api.app.NamesView;
import Button = api.ui.button.Button;
import Element = api.dom.Element;

export class IssueList extends ListBox<IssueSummary> {

    private issueSelectedListeners: {(id: IssueListItem): void}[] = [];

    protected createItemView(issueSummary: IssueSummary): api.dom.Element {

        const itemEl = new IssueListItem(issueSummary, 'issue-list-item');
        itemEl.getEl().setTabIndex(0);

        itemEl.onClicked(() => {
            this.notifyIssueSelected(itemEl);
        });

        if (issueSummary.getDescription()) {
            itemEl.getEl().setTitle(issueSummary.getDescription());
        }

        const namesView: NamesView = new NamesView(false).setMainName(issueSummary.getTitle());
        namesView.setSubNameElements([Element.fromString(this.makeSubName(itemEl))]);

        itemEl.appendChild(namesView);

        return itemEl;
    }

    protected getItemId(issue: IssueSummary): string {
        return issue.getId();
    }

    private makeSubName(issueListItem: IssueListItem): string {
        return '\<span\>#' + issueListItem.getIssue().getId() + ' - ' + issueListItem.getStatusInfo() + '\</span\>';
    }

    onIssueSelected(listener: (id: IssueListItem) => void) {
        this.issueSelectedListeners.push(listener);
    }

    unIssueSelected(listener: (id: IssueListItem) => void) {
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

export class IssueListItem extends api.dom.LiEl {

    private issue: IssueSummary;

    constructor(issue: IssueSummary, className: string) {
        super(className);

        this.issue = issue;
    }

    public getIssue(): IssueSummary {
        return this.issue;
    }

    public getStatusInfo(): string {
        return 'Opened by ' + '\<span class="creator"\>' + this.issue.getCreator() + '\</span\> ' +
               DateHelper.getModifiedString(this.issue.getModifiedTime());
    }
}
