import '../../api.ts';
import {IssueFetcher} from './IssueFetcher';
import {IssueStatsJson} from './IssueStatsJson';
import {IssueListItem} from './IssueList';
import {IssueType} from './IssueType';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import {GetIssueRequest} from './GetIssueRequest';
import {Issue} from './Issue';
import {CreateIssueDialog} from './CreateIssueDialog';
import {IssuesPanel} from './IssuesPanel';

import ModalDialog = api.ui.dialog.ModalDialog;
import DockedPanel = api.ui.panel.DockedPanel;
import Panel = api.ui.panel.Panel;
import TabBarItem = api.ui.tab.TabBarItem;
import LoadMask = api.ui.mask.LoadMask;
import PEl = api.dom.PEl;
import SpanEl = api.dom.SpanEl;
import Element = api.dom.Element;

export class IssuesDialog extends ModalDialog {

    private dockedPanel: DockedPanel;

    private assignedToMeIssuesPanel: IssuesPanel;

    private createdByMeIssuesPanel: IssuesPanel;

    private openIssuesPanel: IssuesPanel;

    private closedIssuesPanel: IssuesPanel;

    private createIssueDialog: CreateIssueDialog;

    constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{title: 'Publishing Issues'});
        this.addClass('issue-list-dialog');
        api.dom.Body.get().appendChild(this);

        this.initIssueDetailsDialog();
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
        dockedPanel.addItem('Open', true,this.openIssuesPanel );
        dockedPanel.addItem('Closed', true, this.closedIssuesPanel);

        return dockedPanel;
    }

    private reloadDockPanel() {
        this.assignedToMeIssuesPanel.reload();
        this.createdByMeIssuesPanel.reload();
        this.openIssuesPanel.reload();
        this.closedIssuesPanel.reload();
    }

    show() {
        this.reload();
        super.show();
    }

    private initIssueDetailsDialog() {
        this.addClickIgnoredElement(IssueDetailsDialog.get());

        IssueDetailsDialog.get().onClosed(() => {
            this.removeClass('masked');
            if (this.isVisible()) {
                this.getEl().focus();
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
        if (stats.assignedToMe > 0) {
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
            if (!this.createIssueDialog) {
                this.createIssueDialog = CreateIssueDialog.get();

                this.createIssueDialog.onClosed(() => {
                    this.removeClass('masked');
                    this.getEl().focus();
                });

                this.createIssueDialog.onSucceed(() => {
                    this.createIssueDialog.reset();
                    this.reload();
                });

                this.addClickIgnoredElement(this.createIssueDialog);
            }

            this.addClass('masked');

            this.createIssueDialog.reset();
            this.createIssueDialog.open();
        });

        return newIssueButton;
    }

    private createIssuePanel(issueType: IssueType): IssuesPanel {
        return new IssuesPanel(issueType);

    }
}
