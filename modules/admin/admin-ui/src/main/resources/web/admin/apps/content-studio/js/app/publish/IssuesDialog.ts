import '../../api.ts';
import {IssueFetcher} from './IssueFetcher';
import {IssueStatsJson} from './IssueStatsJson';
import {IssueSummary} from './IssueSummary';
import {IssueList} from './IssueList';
import {IssueType} from './IssueType';

import ModalDialog = api.ui.dialog.ModalDialog;
import DockedPanel = api.ui.panel.DockedPanel;
import Panel = api.ui.panel.Panel;
import TabBarItem = api.ui.tab.TabBarItem;
import LoadMask = api.ui.mask.LoadMask;
import PEl = api.dom.PEl;

export class IssuesDialog extends ModalDialog {

    private dockedPanel: DockedPanel;

    private assignedToMeIssuesPanel: Panel;
    private createdByMeIssuesPanel: Panel;
    private openIssuesPanel: Panel;
    private closedIssuesPanel: Panel;

    private loadMask: LoadMask;

    constructor() {
        super('Issues');
        this.addClass('issue-list-dialog');
        this.loadMask = new LoadMask(this.getContentPanel());
        api.dom.Body.get().appendChild(this);
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {
            this.appendChildToContentPanel(this.dockedPanel = this.createDockedPanel());
            return rendered;
        });
    }

    private createDockedPanel(): DockedPanel {
        let dockedPanel = new DockedPanel();
        dockedPanel.addItem('Assigned to me', true, this.assignedToMeIssuesPanel = this.createIssuePanel(IssueType.ASSIGNED_TO_ME));
        dockedPanel.addItem('My issues', true, this.createdByMeIssuesPanel = this.createIssuePanel(IssueType.CREATED_BY_ME));
        dockedPanel.addItem('Open', true, this.openIssuesPanel = this.createIssuePanel(IssueType.OPEN));
        dockedPanel.addItem('Closed', true, this.closedIssuesPanel = this.createIssuePanel(IssueType.CLOSED));

        return dockedPanel;
    }

    private createIssuePanel(issueType: IssueType): Panel {
        const panel: Panel = new Panel();

        panel.onShown(() => {
            const panelHasChildren = panel.getChildren().length > 0;

            if (!panelHasChildren && panel.isVisible()) { // to not reload after tab is loaded and swithcing between tabs
                this.loadMask.show();

                IssueFetcher.fetchIssuesByType(issueType).then((issues: IssueSummary[]) => {

                    if (issues.length > 0) {
                        const issueList: IssueList = new IssueList();
                        issueList.addItems(issues);
                        panel.appendChild(issueList);
                    } else {
                        panel.appendChild(new PEl('no-issues-message').setHtml('No issues found'));
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.loadMask.hide();
                });

            }
        });

        return panel;
    }

    show() {
        this.cleanPanels();
        super.show();
        this.appendChildToContentPanel(this.loadMask);
        this.loadMask.show();
        this.reloadIssueData();

    }

    private reloadIssueData() {
        IssueFetcher.fetchIssueStats().then((stats: IssueStatsJson) => {
            this.updateTabLabels(stats);
            this.showFirstNonEmptyTab(stats);
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            this.loadMask.hide();
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

    private cleanPanels() {
        this.assignedToMeIssuesPanel.removeChildren();
        this.createdByMeIssuesPanel.removeChildren();
        this.openIssuesPanel.removeChildren();
        this.closedIssuesPanel.removeChildren();
    }
}
