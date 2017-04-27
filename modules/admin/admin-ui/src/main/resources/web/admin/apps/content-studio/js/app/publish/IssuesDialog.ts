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
import SpanEl = api.dom.SpanEl;
import Element = api.dom.Element;

export class IssuesDialog extends ModalDialog {

    private dockedPanel: DockedPanel;

    private assignedToMeIssuesPanel: Panel;
    private createdByMeIssuesPanel: Panel;
    private openIssuesPanel: Panel;
    private closedIssuesPanel: Panel;

    private loadMask: LoadMask;

    private stats: IssueStatsJson;

    constructor() {
        super('Publishing Issues');
        this.addClass('issue-list-dialog');
        this.loadMask = new LoadMask(this.getContentPanel());
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
        let dockedPanel = new DockedPanel();
        dockedPanel.addItem('Assigned to me', true, this.assignedToMeIssuesPanel = this.createIssuePanel(IssueType.ASSIGNED_TO_ME));
        dockedPanel.addItem('My issues', true, this.createdByMeIssuesPanel = this.createIssuePanel(IssueType.CREATED_BY_ME));
        dockedPanel.addItem('Open', true, this.openIssuesPanel = this.createIssuePanel(IssueType.OPEN));
        dockedPanel.addItem('Closed', true, this.closedIssuesPanel = this.createIssuePanel(IssueType.CLOSED));

        return dockedPanel;
    }

    private createIssuePanel(issueType: IssueType): Panel {
        const panel: Panel = new Panel(IssueType[issueType]);

        panel.onShown(() => {
            const panelHasChildren = panel.getChildren().length > 0;

            if (!panelHasChildren && panel.isVisible()) { // to not reload after tab is loaded and swithcing between tabs
                this.loadMask.show();

                IssueFetcher.fetchIssuesByType(issueType, 0, IssueList.MAX_FETCH_SIZE).then((issues: IssueSummary[]) => {

                    if (issues.length > 0) {
                        const issueList: IssueList = new IssueList(issueType, this.getTotalByType(issueType));
                        issueList.addItems(issues);
                        panel.appendChild(issueList);
                        this.centerMyself();
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
            this.stats = stats;
            this.updateTabLabels();
            this.showFirstNonEmptyTab();
        }).catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            this.loadMask.hide();
        });
    }

    private updateTabLabels() {
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(0), 'Assigned to me', this.stats.assignedToMe);
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(1), 'My issues', this.stats.createdByMe);
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(2), 'Open', this.stats.open);
        this.updateTabLabel(this.dockedPanel.getNavigator().getNavigationItem(3), 'Closed', this.stats.closed);
    }

    private updateTabLabel(tabBarItem: TabBarItem, label: string, issuesFound: number) {
        tabBarItem.setLabel(issuesFound > 0 ? (label + ' (' + issuesFound + ')') : label);
    }

    private showFirstNonEmptyTab() {
        if (this.stats.assignedToMe > 0) {
            this.dockedPanel.selectPanel(this.assignedToMeIssuesPanel);
        } else if (this.stats.createdByMe > 0) {
            this.dockedPanel.selectPanel(this.createdByMeIssuesPanel);
        } else if (this.stats.open > 0) {
            this.dockedPanel.selectPanel(this.openIssuesPanel);
        } else if (this.stats.closed > 0) {
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

    private createNewIssueButton(): Element {
        const newIssueButton: SpanEl = new SpanEl().addClass('new-issue-button');
        newIssueButton.getEl().setTitle('Create an issue');
        return newIssueButton;
    }

    private getTotalByType(type: IssueType): number {
        if (type === IssueType.ASSIGNED_TO_ME) {
            return this.stats.assignedToMe;
        }

        if (type === IssueType.CREATED_BY_ME) {
            return this.stats.createdByMe;
        }

        if (type === IssueType.OPEN) {
            return this.stats.open;
        }

        if (type === IssueType.CLOSED) {
            return this.stats.closed;
        }
    }
}
