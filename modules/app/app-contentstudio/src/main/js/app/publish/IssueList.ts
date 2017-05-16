import '../../api.ts';
import {IssueSummary} from './IssueSummary';
import {IssueType} from './IssueType';
import {IssueFetcher} from './IssueFetcher';
import {IssueResponse} from './IssueResponse';
import ListBox = api.ui.selector.list.ListBox;
import DateHelper = api.util.DateHelper;
import NamesView = api.app.NamesView;
import Button = api.ui.button.Button;
import Element = api.dom.Element;
import LoadMask = api.ui.mask.LoadMask;
import PEl = api.dom.PEl;

export class IssueList extends ListBox<IssueSummary> {

    public static MAX_FETCH_SIZE: number = 10;

    private issueType: IssueType;

    private totalItems: number;

    private scrollHandler: {(): void};

    private loadMask: LoadMask;

    private loading: boolean = false;

    private issueSelectedListeners: {(id: IssueListItem): void}[] = [];

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
        return '\<span\>#' + issueListItem.getIssue().getIndex() + ' - ' + issueListItem.getStatusInfo() + '\</span\>';
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
