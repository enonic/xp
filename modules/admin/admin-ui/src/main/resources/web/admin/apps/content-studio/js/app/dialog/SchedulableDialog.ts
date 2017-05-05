import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import {ProgressBarDialog, ProgressBarConfig} from './ProgressBarDialog';
import {SchedulePublishDialog} from '../publish/SchedulePublishDialog';

export abstract class SchedulableDialog extends ProgressBarDialog {

    private scheduleDialog: SchedulePublishDialog;

    protected showScheduleDialogButton: api.ui.dialog.DialogButton;

    constructor(config: ProgressBarConfig) {
        super(config);
        this.addClass('schedulable-dialog');
    }

    protected getFromDate(): Date {
        return this.scheduleDialog.getFromDate();
    }

    protected getToDate(): Date {
        return this.scheduleDialog.getToDate();
    }

    protected initActions() {
        if (!this.showScheduleDialogButton) {
            const showScheduleAction = new ShowSchedulePublishDialogAction();
            showScheduleAction.onExecuted(this.showScheduleDialog.bind(this));
            this.showScheduleDialogButton = this.addAction(showScheduleAction, false);
            this.showScheduleDialogButton.setTitle('Schedule Publishing');
            this.showScheduleDialogButton.addClass('no-animation');
        }
    }

    protected lockControls() {
        super.lockControls();
        this.showScheduleDialogButton.setEnabled(false);
    }

    protected unlockControls() {
        super.unlockControls();
        this.showScheduleDialogButton.setEnabled(true);
    }

    protected toggleAction(enable: boolean) {
        this.toggleControls(enable);
        this.toggleClass('no-action', !enable);
    }

    private showScheduleDialog() {
        if (!this.scheduleDialog) {
            this.scheduleDialog = new SchedulePublishDialog();
            this.scheduleDialog.onClose(() => {
                this.removeClass('masked');
                this.getEl().focus();
            });
            this.scheduleDialog.onSchedule(() => {
                this.doScheduledAction();
                // this.doPublish(true);
            });
            this.addClickIgnoredElement(this.scheduleDialog);
        }
        this.scheduleDialog.open();
        this.addClass('masked');
    }

    protected countTotal(): number {
        return this.countToPublish(this.getItemList().getItems()) + this.getDependantIds().length;
    }

    private countToPublish(summaries: ContentSummaryAndCompareStatus[]): number {
        return summaries.reduce((count, summary: ContentSummaryAndCompareStatus) => {
            return summary.getCompareStatus() !== CompareStatus.EQUAL ? ++count : count;
        }, 0);
    }

    protected updateShowScheduleDialogButton() {
        if (this.isScheduleButtonAllowed()) {
            this.showScheduleDialogButton.show();
        } else {
            this.showScheduleDialogButton.hide();
        }
    }

    protected doScheduledAction() {
        throw Error('Must be implemented in inheritors');
    }

    protected isScheduleButtonAllowed(): boolean {
        return true;
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}

export class ShowSchedulePublishDialogAction extends api.ui.Action {
    constructor() {
        super();
        this.setIconClass('show-schedule-action');
    }
}
