import {Issue} from '../Issue';
import {IssueResponse} from '../resource/IssueResponse';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import {IssueListDialog} from './IssueListDialog';
import {IssueStatusInfoGenerator} from './IssueStatusInfoGenerator';
import {IssueStatus} from '../IssueStatus';
import {ListIssuesRequest} from '../resource/ListIssuesRequest';
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import User = api.security.User;
import PEl = api.dom.PEl;
import NamesView = api.app.NamesView;
import SpanEl = api.dom.SpanEl;

export class IssueList extends ListBox<Issue> {

    private issueStatus: IssueStatus;

    private totalItems: number;

    private currentUser: User;

    private loadAssignedToMe: boolean = false;

    private loadMyIssues: boolean = false;

    constructor(issueStatus: IssueStatus) {
        super('issue-list');
        this.issueStatus = issueStatus;
        this.loadCurrentUser();
        this.setupLazyLoading();
    }

    public reload(): wemQ.Promise<void> {
        this.removeChildren();
        this.clearItems(true);
        return this.fetchItems();
    }

    setLoadMyIssues(value: boolean) {
        this.loadMyIssues = value;
    }

    setLoadAssignedToMe(value: boolean) {
        this.loadAssignedToMe = value;
    }

    private fetchItems(): wemQ.Promise<void> {
        return this.doFetchItems().then((response: IssueResponse) => {
            this.totalItems = response.getMetadata().getTotalHits();
            if (response.getIssues().length > 0) {
                this.addItems(response.getIssues());
            } else {
                this.appendChild(new PEl('no-issues-message').setHtml('No issues found'));
            }
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });

    }

    private loadCurrentUser() {
        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
            this.currentUser = loginResult.getUser();
        });
    }

    private setupLazyLoading() {
        const scrollHandler: Function = api.util.AppHelper.debounce(this.handleScroll.bind(this), 100, false);

        this.onScrolled(() => {
            scrollHandler();
        });

        this.onScroll(() => {
            scrollHandler();
        });
    }

    private doFetchItems(): wemQ.Promise<IssueResponse> {
        const listIssuesRequest: ListIssuesRequest = new ListIssuesRequest();

        listIssuesRequest.setIssueStatus(this.issueStatus);
        listIssuesRequest.setAssignedToMe(this.loadAssignedToMe);
        listIssuesRequest.setCreatedByMe(this.loadMyIssues);
        listIssuesRequest.setFrom(this.getItemCount());

        return listIssuesRequest.sendAndParse();
    }

    private handleScroll() {
        if (this.isScrolledToBottom() && !this.isAllItemsLoaded()) {
            this.fetchItems();
        }
    }

    protected createItemView(issue: Issue): api.dom.Element {

        const itemEl = new IssueListItem(issue, this.currentUser);

        itemEl.onClicked(() => {
            this.handleIssueSelected(itemEl);
        });

        return itemEl;
    }

    protected getItemId(issue: Issue): string {
        return issue.getId();
    }

    private handleIssueSelected(issueListItem: IssueListItem) {
        IssueListDialog.get().addClass('masked');
        IssueDetailsDialog.get().setIssue(issueListItem.getIssue()).toggleNested(true).open(IssueListDialog.get());
    }

    private isScrolledToBottom(): boolean {
        let element = this.getHTMLElement();
        return (element.scrollHeight - element.scrollTop - 50) <= element.clientHeight; // 50px before bottom to start loading earlier
    }

    private isAllItemsLoaded(): boolean {
        return this.getItemCount() >= this.totalItems;
    }
}

export class IssueListItem extends api.dom.LiEl {

    private issue: Issue;

    private currentUser: User;

    constructor(issue: Issue, currentUser: User) {
        super('issue-list-item');

        this.issue = issue;
        this.currentUser = currentUser;
    }

    public getIssue(): Issue {
        return this.issue;
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered) => {
            this.getEl().setTabIndex(0);

            if (this.issue.getDescription()) {
                this.getEl().setTitle(this.issue.getDescription());
            }

            const namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView
                .setMainName(this.issue.getTitleWithId())
                .setIconClass(this.issue.getIssueStatus() === IssueStatus.CLOSED ? 'icon-signup closed' : 'icon-signup')
                .setSubNameElements([new SpanEl().setHtml(this.makeSubName(), false)]);

            this.appendChild(namesAndIconView);

            return rendered;
        });
    }

    private makeSubName(): string {
        return IssueStatusInfoGenerator.create().setIssue(this.issue).setIssueStatus(this.issue.getIssueStatus()).setCurrentUser(
            this.currentUser).generate();
    }
}
