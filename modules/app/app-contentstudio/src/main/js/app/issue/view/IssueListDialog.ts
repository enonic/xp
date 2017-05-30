import DockedPanel = api.ui.panel.DockedPanel;
import ModalDialog = api.ui.dialog.ModalDialog;
import {IssuesPanel} from './IssuesPanel';
import {ShowIssuesDialogEvent} from '../../browse/ShowIssuesDialogEvent';
import {IssueType} from '../IssueType';
import {IssueDetailsDialog} from './IssueDetailsDialog';
import {UpdateIssueDialog} from './UpdateIssueDialog';
import {Issue} from '../Issue';
import {CreateIssueDialog} from './CreateIssueDialog';
import {IssueServerEventsHandler} from '../event/IssueServerEventsHandler';
import {IssueStatus} from '../IssueStatus';
import TabBarItem = api.ui.tab.TabBarItem;
import SpanEl = api.dom.SpanEl;
import Element = api.dom.Element;
import LoadMask = api.ui.mask.LoadMask;
import User = api.security.User;

export class IssueListDialog extends ModalDialog {

    private static INSTANCE: IssueListDialog = new IssueListDialog();

    private dockedPanel: DockedPanel;

    private assignedToMeIssuesPanel: IssuesPanel;

    private createdByMeIssuesPanel: IssuesPanel;

    private openIssuesPanel: IssuesPanel;

    private closedIssuesPanel: IssuesPanel;

    private reload: Function;

    private loadMask: LoadMask;

    private currentUser: User;

    private constructor() {
        super(<api.ui.dialog.ModalDialogConfig>{title: 'Publishing Issues'});
        this.addClass('issue-list-dialog');

        this.initDeboundcedReloadFunc();
        this.handleIssueDetailsDialogEvents();
        this.handleCreateIssueDialogEvents();
        this.handleIssueGlobalEvents();
        this.initElements();

        ShowIssuesDialogEvent.on((event) => {
            this.open();
        });

        this.loadCurrentUser();
    }

    public static get(): IssueListDialog {
        return IssueListDialog.INSTANCE;
    }

    private loadCurrentUser() {
        return new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult) => {
            this.currentUser = loginResult.getUser();
        });
    }

    private initElements() {
        this.loadMask = new LoadMask(this);
        this.dockedPanel = this.createDockedPanel()
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {
            this.appendChildToContentPanel(this.dockedPanel);
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

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
        this.appendChildToContentPanel(this.loadMask);
        this.reload();
    }

    close() {
        super.close();
        this.remove();
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
    }

    private handleCreateIssueDialogEvents() {
        this.addClickIgnoredElement(CreateIssueDialog.get());

        CreateIssueDialog.get().onClosed(() => {
            this.removeClass('masked');
            this.getEl().focus();
        });
    }

    private initDeboundcedReloadFunc() {
        this.reload = api.util.AppHelper.debounce((issues?: Issue[]) => {
            this.doReload().then(() => {
                this.updateTabLabels();
                this.openTab(issues);
                if (this.isNotificationToBeShown(issues)) {
                    api.notify.NotifyManager.get().showFeedback('The list of issues was updated');
                }
            });
        }, 3000, true);
    }

    private handleIssueGlobalEvents() {

        IssueServerEventsHandler.getInstance().onIssueCreated((issues: Issue[]) => {
            if (this.isVisible()) {
                this.reload(issues);
            }
            else if (issues.some((issue) => this.isIssueCreatedByCurrentUser(issue))) {
                this.open();
            }
        });

        IssueServerEventsHandler.getInstance().onIssueUpdated((issues: Issue[]) => {
            if (this.isVisible()) {
                this.reload(issues);
            }
        });
    }

    private isNotificationToBeShown(issues?: Issue[]): boolean {
        if (!issues) {
            return false;
        }

        if (issues[0].getModifier()) {
            if (this.isIssueModifiedByCurrentUser(issues[0])) {
                return false;
            }

            return true;
        }

        if (this.isIssueCreatedByCurrentUser(issues[0])) {
            return false;
        }

        return true;
    }

    private isIssueModifiedByCurrentUser(issue: Issue): boolean {
        return issue.getModifier() === this.currentUser.getKey().toString();
    }

    private isIssueCreatedByCurrentUser(issue: Issue): boolean {
        if (!issue.getCreator()) {
            return false;
        }

        return issue.getCreator() === this.currentUser.getKey().toString();
    }

    private openTab(issues?: Issue[]) {
        this.dockedPanel.selectPanel(this.getTabToOpen(issues));
    }

    private getTabToOpen(issues?: Issue[]): IssuesPanel {
        if (!issues) {
            return this.getFirstNonEmptyTab();
        }

        if (issues[0].getModifier()) {
            if (this.isIssueModifiedByCurrentUser(issues[0])) {
                if (issues[0].getIssueStatus() === IssueStatus.CLOSED) {
                    return this.closedIssuesPanel;
                }
            }

            return <IssuesPanel>this.dockedPanel.getDeck().getPanelShown();
        }

        if (this.isIssueCreatedByCurrentUser(issues[0])) {
            return this.createdByMeIssuesPanel;
        }

        return <IssuesPanel>this.dockedPanel.getDeck().getPanelShown();
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    private doReload(): wemQ.Promise<void> {
        this.loadMask.show();
        return this.reloadDockPanel().catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            this.loadMask.hide();
        });
    }

    private updateTabLabels() {
        this.updateTabLabel(0, 'Assigned to me', this.assignedToMeIssuesPanel.getTotalItems());
        this.updateTabLabel(1, 'My issues', this.createdByMeIssuesPanel.getTotalItems());
        this.updateTabLabel(2, 'Open', this.openIssuesPanel.getTotalItems());
        this.updateTabLabel(3, 'Closed', this.closedIssuesPanel.getTotalItems());
    }

    private updateTabLabel(tabIndex: number, label: string, issuesFound: number) {
        this.dockedPanel.getNavigator().getNavigationItem(tabIndex).setLabel(issuesFound > 0 ? (label + ' (' + issuesFound + ')') : label);
    }

    private getFirstNonEmptyTab(): IssuesPanel {
        if (this.assignedToMeIssuesPanel.getItemCount() > 0) {
            return this.assignedToMeIssuesPanel;
        } else if (this.createdByMeIssuesPanel.getItemCount() > 0) {
            return this.createdByMeIssuesPanel;
        } else if (this.openIssuesPanel.getItemCount() > 0) {
            return this.openIssuesPanel;
        } else if (this.closedIssuesPanel.getItemCount() > 0) {
            return this.closedIssuesPanel;
        } else {
            return this.assignedToMeIssuesPanel;
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
