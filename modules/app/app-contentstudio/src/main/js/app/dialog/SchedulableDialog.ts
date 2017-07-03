import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import PublishContentRequest = api.content.resource.PublishContentRequest;
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import CompareStatus = api.content.CompareStatus;
import ContentId = api.content.ContentId;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import {ProgressBarConfig, ProgressBarDialog} from './ProgressBarDialog';
import {SchedulePublishDialog} from '../publish/SchedulePublishDialog';
import DropdownButtonRow = api.ui.dialog.DropdownButtonRow;
import i18n = api.util.i18n;

export abstract class SchedulableDialog extends ProgressBarDialog {

    private scheduleDialog: SchedulePublishDialog;

    protected showScheduleAction: ShowSchedulePublishDialogAction;

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
        if (!this.showScheduleAction) {
            this.showScheduleAction = new ShowSchedulePublishDialogAction();
            this.showScheduleAction.onExecuted(this.showScheduleDialog.bind(this));
        }
    }

    protected lockControls() {
        super.lockControls();
        this.showScheduleAction.setEnabled(false);
    }

    protected unlockControls() {
        super.unlockControls();
        this.showScheduleAction.setEnabled(true);
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
        this.showScheduleAction.setEnabled(this.isScheduleButtonAllowed());
    }

    getButtonRow(): DropdownButtonRow {
        return <DropdownButtonRow>super.getButtonRow();
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
        super(i18n('action.scheduleMore'));
        this.setIconClass('show-schedule-action');
    }
}
