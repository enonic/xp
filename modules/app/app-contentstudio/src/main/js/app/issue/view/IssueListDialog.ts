import DockedPanel = api.ui.panel.DockedPanel;
import ModalDialog = api.ui.dialog.ModalDialog;
import {IssuesPanel} from './IssuesPanel';
import {ShowIssuesDialogEvent} from '../../browse/ShowIssuesDialogEvent';
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
import Action = api.ui.Action;
import Checkbox = api.ui.Checkbox;

export class IssueListDialog extends ModalDialog {

    private static INSTANCE: IssueListDialog = new IssueListDialog();

    private dockedPanel: DockedPanel;

    private assignedToMeCheckbox: Checkbox;

    private myIssuesCheckbox: Checkbox;

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

    isAssignedToMeChecked(): boolean {
        return this.assignedToMeCheckbox.isChecked();
    }

    isMyIssuesChecked(): boolean {
        return this.myIssuesCheckbox.isChecked();
    }

    private initElements() {
        this.assignedToMeCheckbox = this.createAssignedToMeCheckbox();
        this.myIssuesCheckbox = this.createMyIssuesCheckbox();
        this.loadMask = new LoadMask(this);
        this.openIssuesPanel = this.createIssuePanel(IssueStatus.OPEN);
        this.closedIssuesPanel = this.createIssuePanel(IssueStatus.CLOSED);
        this.dockedPanel = this.createDockedPanel();
    }

    doRender(): Q.Promise<boolean> {
        return super.doRender().then((rendered: boolean) => {
            this.createNewIssueButton();
            this.appendChildToContentPanel(this.assignedToMeCheckbox);
            this.appendChildToContentPanel(this.myIssuesCheckbox);
            this.appendChildToContentPanel(this.dockedPanel);
            return rendered;
        });
    }

    private createDockedPanel(): DockedPanel {
        const dockedPanel = new DockedPanel();

        dockedPanel.addItem('Open', true, this.openIssuesPanel);
        dockedPanel.addItem('Closed', true, this.closedIssuesPanel);

        return dockedPanel;
    }

    private createAssignedToMeCheckbox(): Checkbox {
        const assignedToMeCheckbox: Checkbox = Checkbox.create().build();
        assignedToMeCheckbox.addClass('assigned-to-me-filter');
        assignedToMeCheckbox.onValueChanged(() => {
            this.doReload();
        });
        assignedToMeCheckbox.setLabel('Assigned to Me');

        return assignedToMeCheckbox;
    }

    private createMyIssuesCheckbox(): Checkbox {
        const myIssuesCheckbox: Checkbox = Checkbox.create().build();
        myIssuesCheckbox.addClass('my-issues-filter');
        myIssuesCheckbox.onValueChanged(() => {
            this.doReload();
        });
        myIssuesCheckbox.setLabel('My Issues');

        return myIssuesCheckbox;
    }

    private reloadDockPanel(): wemQ.Promise<any> {
        let promises: wemQ.Promise<any>[] = [
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
            this.doReload(issues);
        }, 3000, true);
    }

    private doReload(updatedIssues?: Issue[]) {
        this.loadData().then(() => {
            this.updateTabLabels();
            this.updateFilters();
            this.openTab(this.getTabToOpen(updatedIssues));
            if (this.isNotificationToBeShown(updatedIssues)) {
                api.notify.NotifyManager.get().showFeedback('The list of issues was updated');
            }
        });
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

    private openTab(issuePanel: IssuesPanel) {
        this.dockedPanel.selectPanel(issuePanel);
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
            return this.openIssuesPanel;
        }

        return <IssuesPanel>this.dockedPanel.getDeck().getPanelShown();
    }

    protected hasSubDialog(): boolean {
        return true;
    }

    private loadData(): wemQ.Promise<void> {
        this.loadMask.show();
        return this.reloadDockPanel().catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            this.loadMask.hide();
        });
    }

    private updateTabLabels() {
        this.updateTabLabel(0, 'Open', this.openIssuesPanel.getTotalItems());
        this.updateTabLabel(1, 'Closed', this.closedIssuesPanel.getTotalItems());
    }

    private updateTabLabel(tabIndex: number, label: string, issuesFound: number) {
        this.dockedPanel.getNavigator().getNavigationItem(tabIndex).setLabel(issuesFound > 0 ? (label + ' (' + issuesFound + ')') : label);
    }

    private updateFilters() {
        const noData: boolean = this.openIssuesPanel.getTotalItems() === 0 && this.closedIssuesPanel.getTotalItems() === 0;

        if (noData) {
            if (!this.myIssuesCheckbox.isChecked()) {
                this.myIssuesCheckbox.addClass('disabled');
            }
            if (!this.assignedToMeCheckbox.isChecked()) {
                this.assignedToMeCheckbox.addClass('disabled');
            }
        }
        else {
            this.assignedToMeCheckbox.removeClass('disabled');
            this.myIssuesCheckbox.removeClass('disabled');
        }
    }

    private getFirstNonEmptyTab(): IssuesPanel {
        if (this.openIssuesPanel.getItemCount() > 0) {
            return this.openIssuesPanel;
        } else if (this.closedIssuesPanel.getItemCount() > 0) {
            return this.closedIssuesPanel;
        }

        return this.openIssuesPanel;
    }

    private createNewIssueButton(): Element {
        let createIssueAction = new Action('New Issue...');

        createIssueAction.onExecuted(() => {
            this.addClass('masked');
            let createIssueDialog = CreateIssueDialog.get();

            createIssueDialog.enableCancelButton();
            createIssueDialog.reset();
            createIssueDialog.unlockPublishItems();
            createIssueDialog.open(this);
        });
        
        return this.getButtonRow().addAction(createIssueAction);
    }

    private createIssuePanel(issueStatus: IssueStatus): IssuesPanel {
        return new IssuesPanel(issueStatus);
    }
}
