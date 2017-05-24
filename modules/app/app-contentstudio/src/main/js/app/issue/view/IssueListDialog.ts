import DockedPanel = api.ui.panel.DockedPanel;
import ModalDialog = api.ui.dialog.ModalDialog;
import {IssuesPanel} from './IssuesPanel';
import {ShowIssuesDialogEvent} from '../../browse/ShowIssuesDialogEvent';
import {IssueType} from '../IssueType';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import {UpdateIssueDialog} from './UpdateIssueDialog';
import {Issue} from '../Issue';
import {CreateIssueDialog} from './CreateIssueDialog';
import {IssueListItem} from './IssueList';
import {IssueFetcher} from '../IssueFetcher';
import {IssueStatsJson} from '../json/IssueStatsJson';
import {GetIssueRequest} from '../resource/GetIssueRequest';
import TabBarItem = api.ui.tab.TabBarItem;
import SpanEl = api.dom.SpanEl;
import Element = api.dom.Element;
import IssueServerEventsHandler = api.issue.event.IssueServerEventsHandler;
import LoadMask = api.ui.mask.LoadMask;

export class IssueListDialog extends ModalDialog {

    private dockedPanel: DockedPanel;

    private assignedToMeIssuesPanel: IssuesPanel;

    private createdByMeIssuesPanel: IssuesPanel;

    private openIssuesPanel: IssuesPanel;

    private closedIssuesPanel: IssuesPanel;

    private showCreatedByMePanelAfterLoad: boolean = false;

    private reload: Function;

    private loadMask: LoadMask;

    constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{title: 'Publishing Issues'});
        this.addClass('issue-list-dialog');

        this.initDeboundcedReloadFunc();
        this.handleIssueDetailsDialogEvents();
        this.handleCreateIssueDialogEvents();
        this.handleIssueGlobalEvents();

        ShowIssuesDialogEvent.on((event) => {
            this.open();
        });

        api.dom.Body.get().appendChild(this);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {
            this.appendChildToContentPanel(this.dockedPanel = this.createDockedPanel());
            this.appendChildToContentPanel(this.createNewIssueButton());
            this.getContentPanel().getParentElement().appendChild(this.loadMask = new LoadMask(this));
            return rendered;
        });
    }

    private createDockedPanel(): DockedPanel {
        const dockedPanel = new DockedPanel();

        this.assignedToMeIssuesPanel = this.createIssuePanel(IssueType.ASSIGNED_TO_ME);
        this.createdByMeIssuesPanel = this.createIssuePanel(IssueType.CREATED_BY_ME);
        this.openIssuesPanel = this.createIssuePanel(IssueType.OPEN);
        this.closedIssuesPanel = this.createIssuePanel(IssueType.CLOSED);

        this.assignedToMeIssuesPanel.onIssueSelected(this.showIssueDetailsDialog.bind(this));
        this.createdByMeIssuesPanel.onIssueSelected(this.showIssueDetailsDialog.bind(this));
        this.openIssuesPanel.onIssueSelected(this.showIssueDetailsDialog.bind(this));
        this.closedIssuesPanel.onIssueSelected(this.showIssueDetailsDialog.bind(this));

        dockedPanel.addItem('Assigned to me', true, this.assignedToMeIssuesPanel);
        dockedPanel.addItem('My issues', true, this.createdByMeIssuesPanel);
        dockedPanel.addItem('Open', true, this.openIssuesPanel);
        dockedPanel.addItem('Closed', true, this.closedIssuesPanel);

        return dockedPanel;
    }

    private reloadDockPanel(): wemQ.Promise<any> {
        let promises: wemQ.Promise<any>[] = [
            this.assignedToMeIssuesPanel.reload(),
            this.createdByMeIssuesPanel.reload(),
            this.openIssuesPanel.reload(),
            this.closedIssuesPanel.reload()
        ]

        return wemQ.all(promises);
    }

    private refreshDockPanel() {
        this.assignedToMeIssuesPanel.refresh();
        this.createdByMeIssuesPanel.refresh();
        this.openIssuesPanel.refresh();
        this.closedIssuesPanel.refresh();
    }

    show() {
        super.show();
        this.reload();
    }

    private handleIssueDetailsDialogEvents() {
        this.addClickIgnoredElement(IssueDetailsDialog.get());
        this.addClickIgnoredElement(UpdateIssueDialog.get());

        IssueDetailsDialog.get().onClosed(() => {
            this.removeClass('masked');
            if (this.isVisible()) {
                this.getEl().focus();
            }
        });

        IssueDetailsDialog.get().onIssueClosed((issue: Issue) => {
            this.refresh(issue).then(() => {
                this.dockedPanel.selectPanel(this.closedIssuesPanel);
            });
        });
    }

    private handleCreateIssueDialogEvents() {
        this.addClickIgnoredElement(CreateIssueDialog.get());

        CreateIssueDialog.get().onClosed(() => {
            this.removeClass('masked');
            this.getEl().focus();
        });

        CreateIssueDialog.get().onSucceed(() => {
            this.showCreatedByMePanelAfterLoad = true;
            if (!this.isVisible()) {
                this.open();
            }
        });
    }

    private initDeboundcedReloadFunc() {
        this.reload = api.util.AppHelper.debounce((showNotification: boolean = false) => {
            this.doReload().then(() => {
                if (showNotification) {
                    api.notify.NotifyManager.get().showFeedback('The list of issues was updated');
                }
            });
        }, 3000, true);
    }

    private handleIssueGlobalEvents() {

        IssueServerEventsHandler.getInstance().onIssueCreated(() => {
            if (this.isVisible()) {
                this.reload(true);
            }
        });

        IssueServerEventsHandler.getInstance().onIssueUpdated(() => {
            if (this.isVisible()) {
                this.reload(true);
            }
        });
    }

    showIssueDetailsDialog(issueListItem: IssueListItem) {
        this.addClass('masked');

        new GetIssueRequest(issueListItem.getIssue().getId()).sendAndParse().then((issue: Issue) => {
            IssueDetailsDialog.get().setIssue(issue).toggleNested(true).open();
        });
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    private doReload(): wemQ.Promise<void> {
        this.loadMask.show();
        return IssueFetcher.fetchIssueStats().then((stats: IssueStatsJson) => {
            this.updateTabLabels(stats);
            this.showFirstNonEmptyTab(stats);
            return this.reloadDockPanel();
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            this.loadMask.hide();
        });
    }

    public refresh(issue: Issue): wemQ.Promise<void> {
        return IssueFetcher.fetchIssueStats().then((stats: IssueStatsJson) => {
            this.updateTabLabels(stats);

            // Refresh panels
            this.openIssuesPanel.getIssuesList().removeItem(issue);

            this.closedIssuesPanel.getIssuesList().replaceItem(issue, true);
            this.assignedToMeIssuesPanel.getIssuesList().replaceItem(issue, true);
            this.createdByMeIssuesPanel.getIssuesList().replaceItem(issue, true);

            this.refreshDockPanel();
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        });
    }

    private updateTabLabels(stats: IssueStatsJson) {
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(0), 'Assigned to me', stats.assignedToMe);
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(1), 'My issues', stats.createdByMe);
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(2), 'Open', stats.open);
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(3), 'Closed', stats.closed);
    }

    private updateTabLabel(tabBarItem: TabBarItem, label: string, issuesFound: number) {
        tabBarItem.setLabel(issuesFound > 0 ? (label + ' (' + issuesFound + ')') : label);
    }

    private showFirstNonEmptyTab(stats: IssueStatsJson) {
        if (this.showCreatedByMePanelAfterLoad) {
            this.dockedPanel.selectPanel(this.createdByMeIssuesPanel);
            this.showCreatedByMePanelAfterLoad = false;
        } else if (stats.assignedToMe > 0) {
            this.dockedPanel.selectPanel(this.assignedToMeIssuesPanel);
        } else if (stats.createdByMe > 0) {
            this.dockedPanel.selectPanel(this.createdByMeIssuesPanel);
        } else if (stats.open > 0) {
            this.dockedPanel.selectPanel(this.openIssuesPanel);
        } else if (stats.closed > 0) {
            this.dockedPanel.selectPanel(this.closedIssuesPanel);
        } else {
            this.dockedPanel.selectPanel(this.assignedToMeIssuesPanel);
        }
    }

    private createNewIssueButton(): Element {
        const newIssueButton: SpanEl = new SpanEl().addClass('new-issue-button');
        newIssueButton.getEl().setTitle('Create an issue');

        newIssueButton.onClicked(() => {
            this.addClass('masked');

            CreateIssueDialog.get().reset();

            CreateIssueDialog.get().unlockPublishItems();
            CreateIssueDialog.get().open();
        });

        return newIssueButton;
    }

    private createIssuePanel(issueType: IssueType): IssuesPanel {
        return new IssuesPanel(issueType);

    }
}
