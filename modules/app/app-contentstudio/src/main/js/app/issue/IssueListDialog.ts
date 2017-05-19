import '../../api.ts';
import {IssueListItem} from './IssueList';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import {CreateIssueDialog} from './CreateIssueDialog';
import {IssuesPanel} from './IssuesPanel';
import {ShowIssuesDialogEvent} from '../browse/ShowIssuesDialogEvent';

import ModalDialog = api.ui.dialog.ModalDialog;
import DockedPanel = api.ui.panel.DockedPanel;
import Panel = api.ui.panel.Panel;
import TabBarItem = api.ui.tab.TabBarItem;
import LoadMask = api.ui.mask.LoadMask;
import PEl = api.dom.PEl;
import SpanEl = api.dom.SpanEl;
import Element = api.dom.Element;
import {UpdateIssueDialog} from "../publish/UpdateIssueDialog";
import IssueType = api.issue.IssueType;
import Issue = api.issue.Issue;
import IssueStatsJson = api.issue.json.IssueStatsJson;
import IssueFetcher = api.issue.IssueFetcher;
import GetIssueRequest = api.issue.resource.GetIssueRequest;

export class IssueListDialog extends ModalDialog {

    private dockedPanel: DockedPanel;

    private assignedToMeIssuesPanel: IssuesPanel;

    private createdByMeIssuesPanel: IssuesPanel;

    private openIssuesPanel: IssuesPanel;

    private closedIssuesPanel: IssuesPanel;

    private showCreatedByMePanelAfterLoad: boolean = false;

    constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{title: 'Publishing Issues'});
        this.addClass('issue-list-dialog');

        this.initIssueDetailsDialog();
        this.initCreateIssueDialog();

        ShowIssuesDialogEvent.on((event) => {
            this.open();
        });

        api.dom.Body.get().appendChild(this);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {
            this.appendChildToContentPanel(this.dockedPanel = this.createDockedPanel());
            this.appendChildToContentPanel(this.createNewIssueButton());
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
        dockedPanel.addItem('Open', true, this.openIssuesPanel );
        dockedPanel.addItem('Closed', true, this.closedIssuesPanel);

        return dockedPanel;
    }

    private reloadDockPanel() {
        this.assignedToMeIssuesPanel.reload();
        this.createdByMeIssuesPanel.reload();
        this.openIssuesPanel.reload();
        this.closedIssuesPanel.reload();
    }

    private refreshDockPanel() {
        this.assignedToMeIssuesPanel.refresh();
        this.createdByMeIssuesPanel.refresh();
        this.openIssuesPanel.refresh();
        this.closedIssuesPanel.refresh();
    }

    show() {
        this.reload();
        super.show();
    }

    private initIssueDetailsDialog() {
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

    private initCreateIssueDialog() {
        this.addClickIgnoredElement(CreateIssueDialog.get());

        CreateIssueDialog.get().onClosed(() => {
            this.removeClass('masked');
            this.getEl().focus();
        });

        CreateIssueDialog.get().onSucceed(() => {
            CreateIssueDialog.get().reset();
            this.showCreatedByMePanelAfterLoad = true;
            if (this.isVisible()) {
                this.reload();
            } else {
                this.open();
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

    public reload() {
        IssueFetcher.fetchIssueStats().then((stats: IssueStatsJson) => {
            this.updateTabLabels(stats);
            this.showFirstNonEmptyTab(stats);
            this.reloadDockPanel();
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
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
