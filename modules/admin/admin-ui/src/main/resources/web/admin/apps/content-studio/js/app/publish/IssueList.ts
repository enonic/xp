import '../../api.ts';
import {IssueSummary} from './IssueSummary';
import ListBox = api.ui.selector.list.ListBox;
import DateHelper = api.util.DateHelper;
import NamesView = api.app.NamesView;
import Button = api.ui.button.Button;
import Element = api.dom.Element;

export class IssueList extends ListBox<IssueSummary> {

    private issueSelectedListeners: {(id: string): void}[] = [];

    protected createItemView(issue: IssueSummary): api.dom.Element {
        let namesView: NamesView = new NamesView(false).setMainName(issue.getTitle());
        namesView.setSubNameElements([Element.fromString(this.makeSubName(issue))]);

        let itemEl = new api.dom.LiEl('issue-list-item');
        itemEl.getEl().setTabIndex(0);
        itemEl.appendChild(namesView);

        itemEl.onDblClicked(() => {
            this.notifyIssueSelected(issue.getId());
        });

        if (issue.getDescription()) {
            itemEl.getEl().setTitle(issue.getDescription());
        }

        return itemEl;
    }

    protected getItemId(issue: IssueSummary): string {
        return issue.getId();
    }

    private makeSubName(issue: IssueSummary): string {
        return '\<span\>#' + issue.getId() + ' - Opened by ' + '\<span class="creator"\>' + issue.getCreator() + '\</span\> ' +
               DateHelper.getModifiedString(issue.getModifiedTime()) + '\</span\>';
    }

    onIssueSelected(listener: (id: string) => void) {
        this.issueSelectedListeners.push(listener);
    }

    unIssueSelected(listener: (id: string) => void) {
        this.issueSelectedListeners = this.issueSelectedListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyIssueSelected(id: string) {
        this.issueSelectedListeners.forEach(listener => {
            listener(id);
        });
    }
}
