import '../../api.ts';
import {IssueType} from './IssueType';
import {IssueFetcher} from './IssueFetcher';
import {IssueResponse} from './IssueResponse';
import {Issue} from './Issue';
import ListBox = api.ui.selector.list.ListBox;
import DateHelper = api.util.DateHelper;
import NamesView = api.app.NamesView;
import Button = api.ui.button.Button;
import Element = api.dom.Element;
import LoadMask = api.ui.mask.LoadMask;
import PEl = api.dom.PEl;
import User = api.security.User;
import SpanEl = api.dom.SpanEl;

export class IssueList extends ListBox<Issue> {

    public static MAX_FETCH_SIZE: number = 10;

    private issueType: IssueType;

    private totalItems: number;

    private scrollHandler: {(): void};

    private loadMask: LoadMask;

    private loading: boolean = false;

    private issueSelectedListeners: {(id: IssueListItem): void}[] = [];

    private currentUser: User;

    constructor(issueType: IssueType) {
        super('issue-list');
        this.issueType = issueType;
        this.appendChild(this.loadMask = new LoadMask(this));
        this.onRendered(this.initList.bind(this));
        this.setupLazyLoading();
    }

    public reload() {
        this.removeChildren();
        this.clearItems(true);
        this.initList();
    }

    refreshList() {
        super.refreshList();
        if (this.getItemCount() === 0) {
            this.appendChild(new PEl('no-issues-message').setHtml('No issues found'));
        }
    }

    private initList() {
        this.loadMask.show();

        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
            this.currentUser = loginResult.getUser();

            IssueFetcher.fetchIssuesByType(this.issueType, 0, IssueList.MAX_FETCH_SIZE).then((response: IssueResponse) => {
                this.totalItems = response.getMetadata().getTotalHits();
                if (response.getIssues().length > 0) {
                    this.addItems(response.getIssues());
                } else {
                    this.appendChild(new PEl('no-issues-message').setHtml('No issues found'));
                }
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.loadMask.hide();
            });
        });
    }

    private setupLazyLoading() {
        this.scrollHandler = api.util.AppHelper.debounce(this.handleScroll.bind(this), 100, false);

        this.onScrolled(() => {
            this.scrollHandler();
        });

        this.onScroll(() => {
            this.scrollHandler();
        });
    }

    private handleScroll() {
        if (this.isScrolledToBottom() && !this.isAllItemsLoaded()) {
            this.loadMask.show();
            this.loading = true;

            IssueFetcher.fetchIssuesByType(this.issueType, this.getItemCount(), IssueList.MAX_FETCH_SIZE).then(
                (response: IssueResponse) => {
                    this.addItems(response.getIssues());
                }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.loading = false;
                this.loadMask.hide();
            });
        }
    }

    protected createItemView(issue: Issue): api.dom.Element {

        const itemEl = new IssueListItem(issue, this.issueType, this.currentUser);

        itemEl.onClicked(() => {
            this.notifyIssueSelected(itemEl);
        });

        return itemEl;
    }

    protected getItemId(issue: Issue): string {
        return issue.getId();
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

    private issueType: IssueType;

    private currentUser: User;

    private assignedToMePattern: string = '#{0} - Opened by {1} {2}';

    private createdByMePattern: string = '#{0} - Opened {1}. Assigned to {2}';

    private openAndClosedPattern: string = '#{0} - Opened by {1} {2}. Assigned to {3}';

    constructor(issue: Issue, issueType: IssueType, currentUser: User) {
        super('issue-list-item');

        this.issue = issue;
        this.issueType = issueType;
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

            const namesView: NamesView = new NamesView(false).setMainName(this.issue.getTitle());
            namesView.setSubNameElements([new SpanEl().setHtml(this.makeSubName(), false)]);

            this.appendChild(namesView);

            return rendered;
        });
    }

    private makeSubName(): string {
        const modifiedDateString: string = DateHelper.getModifiedString(this.issue.getModifiedTime());

        if (this.issueType === IssueType.ASSIGNED_TO_ME) {
            return api.util.StringHelper.format(this.assignedToMePattern, this.issue.getIndex(), this.modifiedBy(), modifiedDateString);
        }

        if (this.issueType === IssueType.OPEN || this.issueType === IssueType.CLOSED) {
            return api.util.StringHelper.format(this.openAndClosedPattern, this.issue.getIndex(), this.modifiedBy(), modifiedDateString,
                this.assignedTo());
        }

        return api.util.StringHelper.format(this.createdByMePattern, this.issue.getIndex(), modifiedDateString, this.assignedTo());
    }

    private modifiedBy(): string {
        return '\<span class="creator"\>' + this.getModifiedBy() + '\</span\>'
    }

    private getModifiedBy(): string {
        if (this.issue.getCreator() === this.currentUser.getKey().toString()) {
            return 'me';
        }

        return this.issue.getCreator();
    }

    private assignedTo(): string {
        return '\<span class="assignee"\>' + this.getAssignedTo() + '\</span\>' + (this.issue.getApprovers().length > 1
                ? ' users'
                : '');
    }

    private getAssignedTo(): string {
        if (this.issue.getApprovers().length > 1) {
            return this.issue.getApprovers().length.toString();
        }

        const assignee: string = this.issue.getApprovers()[0].toString();

        if (assignee === this.currentUser.getKey().toString()) {
            return 'me';
        }

        return this.issue.getApprovers()[0].toString();
    }

}
