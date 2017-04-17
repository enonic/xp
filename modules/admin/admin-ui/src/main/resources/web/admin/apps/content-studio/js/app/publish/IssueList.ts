import '../../api.ts';
import {IssueSummary} from './IssueSummary';
import ListBox = api.ui.selector.list.ListBox;
import DateHelper = api.util.DateHelper;
import NamesAndIconView = api.app.NamesAndIconView;
import NamesView = api.app.NamesView;
import Button = api.ui.button.Button;

export class IssueList extends ListBox<IssueSummary> {

    protected createItemView(issue: IssueSummary): api.dom.Element {
        let namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
        namesAndIconView
            .setMainName(issue.getTitle())
            .setIconClass('icon-signup')
            .setSubName(this.makeSubName(issue));

        let itemEl = new api.dom.LiEl('issue-list-item');
        itemEl.getEl().setTabIndex(0);
        itemEl.appendChild(namesAndIconView);
        itemEl.appendChild(new Button().setLabel('Open'));
        return itemEl;
    }

    protected getItemId(issue: IssueSummary): string {
        return issue.getId();
    }

    private makeSubName(issue: IssueSummary): string {
        return '#' + issue.getId() + ' Opened by ' + issue.getCreator() + ' ' + DateHelper.getModifiedString(issue.getModifiedTime());
    }
}
