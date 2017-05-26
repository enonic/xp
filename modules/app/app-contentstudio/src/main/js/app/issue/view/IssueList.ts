import {IssueType} from '../IssueType';
import {Issue} from '../Issue';
import {IssueFetcher} from '../IssueFetcher';
import {IssueResponse} from '../resource/IssueResponse';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import {IssueListDialog} from './IssueListDialog';
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import User = api.security.User;
import PEl = api.dom.PEl;
import NamesView = api.app.NamesView;
import SpanEl = api.dom.SpanEl;
import DateHelper = api.util.DateHelper;

export class IssueList extends ListBox<Issue> {

    public static MAX_FETCH_SIZE: number = 10;

    private issueType: IssueType;

    private totalItems: number;

    private loadMask: LoadMask;

    private currentUser: User;

    constructor(issueType: IssueType) {
        super('issue-list');
        this.issueType = issueType;
        this.appendChild(this.loadMask = new LoadMask(this));
        this.onRendered(this.initList.bind(this));
        this.loadCurrentUser();
        this.setupLazyLoading();
    }

    public reload(): wemQ.Promise<void> {
        this.removeChildren();
        this.clearItems(true);
        return this.initList();
    }

    refreshList() {
        super.refreshList();
        if (this.getItemCount() === 0) {
            this.appendChild(new PEl('no-issues-message').setHtml('No issues found'));
        }
    }

    private initList(): wemQ.Promise<void> {
        this.loadMask.show();

        return IssueFetcher.fetchIssuesByType(this.issueType, 0, IssueList.MAX_FETCH_SIZE).then((response: IssueResponse) => {
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

    }

    public getTotalItems(): number {
        return this.totalItems;
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

    private handleScroll() {
        if (this.isScrolledToBottom() && !this.isAllItemsLoaded()) {
            this.loadMask.show();

            IssueFetcher.fetchIssuesByType(this.issueType, this.getItemCount(), IssueList.MAX_FETCH_SIZE).then(
                (response: IssueResponse) => {
                    this.addItems(response.getIssues());
                }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.loadMask.hide();
            });
        }
    }

    protected createItemView(issue: Issue): api.dom.Element {

        const itemEl = new IssueListItem(issue, this.issueType, this.currentUser);

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
        IssueDetailsDialog.get().setIssue(issueListItem.getIssue()).toggleNested(true).open();
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
        return IssueListItemSubNameGenerator.create().setIssue(this.issue).setIssueType(this.issueType).setCurrentUser(
            this.currentUser).generate();
    }
}

export class IssueListItemSubNameGenerator {

    private issue: Issue;

    private issueType: IssueType;

    private currentUser: User;

    private assignedToMePattern: string = '#{0} - {1} by {2} {3}'; // id, status, modifier, date

    private createdByMePattern: string = '#{0} - {1} {2}. Assigned to {3}'; //id, status, date, assignees

    private openPattern: string = '#{0} - {1} by {2} {3}. Assigned to {4}'; //id, status, modifier, date, assignees

    private closedPattern: string = '#{0} - Closed by {1} {2}. Assigned to {3}'; //id, modifier, date, assignees

    private constructor() {
    }

    public static create(): IssueListItemSubNameGenerator {
        return new IssueListItemSubNameGenerator();
    }

    public setIssue(issue: Issue): IssueListItemSubNameGenerator {
        this.issue = issue;
        return this;
    }

    public setIssueType(issueType: IssueType): IssueListItemSubNameGenerator {
        this.issueType = issueType;
        return this;
    }

    public setCurrentUser(currentUser: User): IssueListItemSubNameGenerator {
        this.currentUser = currentUser;
        return this;
    }

    public generate(): string {
        const modifiedDateString: string = DateHelper.getModifiedString(this.issue.getModifiedTime());

        if (this.issueType === IssueType.ASSIGNED_TO_ME) {
            return api.util.StringHelper.format(this.assignedToMePattern, this.issue.getIndex(), this.getStatus(), this.getLastModifiedBy(),
                modifiedDateString);
        }

        if (this.issueType === IssueType.OPEN) {
            return api.util.StringHelper.format(this.openPattern, this.issue.getIndex(), this.getStatus(), this.getLastModifiedBy(),
                modifiedDateString, this.assignedTo());
        }

        if (this.issueType === IssueType.CLOSED) {
            return api.util.StringHelper.format(this.closedPattern, this.issue.getIndex(), this.getLastModifiedBy(), modifiedDateString,
                this.assignedTo());
        }

        return api.util.StringHelper.format(this.createdByMePattern, this.issue.getIndex(), this.getStatus(), modifiedDateString,
            this.assignedTo());
    }

    private getStatus(): string {
        if (this.issue.getModifier()) {
            return 'Updated'
        }

        return 'Opened';
    }

    private getLastModifiedBy(): string {
        return '\<span class="creator"\>' + this.getModifiedBy() + '\</span\>'
    }

    private getModifiedBy(): string {
        const lastModifiedBy: string = !!this.issue.getModifier() ? this.issue.getModifier() : this.issue.getCreator();

        if (lastModifiedBy === this.currentUser.getKey().toString()) {
            return 'me';
        }

        return lastModifiedBy;
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
