import '../../api.ts';
import {IssueSummary} from './IssueSummary';
import {IssueType} from './IssueType';
import {IssueFetcher} from './IssueFetcher';
import ListBox = api.ui.selector.list.ListBox;
import DateHelper = api.util.DateHelper;
import NamesView = api.app.NamesView;
import Button = api.ui.button.Button;
import Element = api.dom.Element;
import LoadMask = api.ui.mask.LoadMask;

export class IssueList extends ListBox<IssueSummary> {

    public static MAX_FETCH_SIZE: number = 10;

    private issueType: IssueType;

    private totalItems: number;

    private scrollHandler: {(): void};

    private loadMask: LoadMask;

    private loading: boolean = false;

    constructor(issueType: IssueType, total: number) {
        super('issue-list');
        this.issueType = issueType;
        this.totalItems = total;
        this.appendChild(this.loadMask = new LoadMask(this));

        this.setupLazyLoading();
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

            IssueFetcher.fetchIssuesByType(this.issueType, this.getItemCount(), IssueList.MAX_FETCH_SIZE).then((issues: IssueSummary[]) => {
                this.addItems(issues);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.loading = false;
                this.loadMask.hide();
            });
        }
    }

    protected createItemView(issue: IssueSummary): api.dom.Element {
        let namesView: NamesView = new NamesView(false).setMainName(issue.getTitle());
        namesView.setSubNameElements([Element.fromString(this.makeSubName(issue))]);

        let itemEl = new api.dom.LiEl('issue-list-item');
        itemEl.getEl().setTabIndex(0);
        itemEl.appendChild(namesView);

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

    private isScrolledToBottom(): boolean {
        let element = this.getHTMLElement();
        return (element.scrollHeight - element.scrollTop - 50) <= element.clientHeight; // 50px before bottom to start loading earlier
    }

    private isAllItemsLoaded(): boolean {
        return this.getItemCount() >= this.totalItems;
    }

}
