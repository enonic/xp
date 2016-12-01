import "../../api.ts";

import DateTimePickerBuilder = api.ui.time.DateTimePickerBuilder;
import DateTimePicker = api.ui.time.DateTimePicker;

export class SchedulePublishDialog extends api.ui.dialog.ModalDialog {

    private confirmDeleteButton: api.ui.dialog.DialogButton;

    private confirmScheduleAction: api.ui.Action;

    private fromDate: DateTimePicker;

    private toDate: DateTimePicker;

    private onCloseCallback: () => void;

    private onScheduleCallback: () => void;

    constructor() {
        super("Scheduled Publishing");

        this.getEl().addClass("schedule-publish-dialog");

        this.addSubtitle();

        this.initConfirmScheduleAction();

        this.initScheduleInputs();

        this.addCancelButtonToBottom("Back");
    }

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
    }

    close() {
        super.close();
        this.remove();
        if (this.onCloseCallback) {
            this.onCloseCallback();
        }
    }

    onClose(onCloseCallback: () => void) {
        this.onCloseCallback = onCloseCallback;
    }

    onSchedule(onScheduleCallback: () => void) {
        this.onScheduleCallback = onScheduleCallback;
    }

    private addSubtitle() {
        this.appendChildToHeader(new api.dom.H6El("schedule-publish-dialog-subtitle").setHtml(
            "<b>Plan ahead!</b> Specify the exact time for to publish this batch of items, optionally specify when to archive items too - we'll keep you notified!",
            false));
    }

    private initConfirmScheduleAction() {
        this.confirmScheduleAction = new api.ui.Action("Schedule");

        this.confirmScheduleAction.setIconClass("confirm-schedule-action");
        this.confirmScheduleAction.onExecuted(() => {
            this.close();
            if (this.onScheduleCallback) {
                this.onScheduleCallback();
            }
        });

        this.confirmDeleteButton = this.addAction(this.confirmScheduleAction, true, true);
    }

    private initScheduleInputs() {

        var publishFromDateTimeBuilder = new DateTimePickerBuilder();

        this.fromDate = publishFromDateTimeBuilder.build();

        this.fromDate.onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.validate();
        });

        this.fromDate.setSelectedDateTime(new Date());

        this.toDate = new DateTimePickerBuilder().build();

        this.toDate.onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.validate();
        });

        var publishFromLabel = new api.dom.LabelEl("Publish From", this.fromDate, "publish-from-label"),
            publishToLabel = new api.dom.LabelEl("Publish To", this.toDate, "publish-to-label");

        this.appendChildToContentPanel(publishFromLabel);
        this.appendChildToContentPanel(this.fromDate);
        this.appendChildToContentPanel(publishToLabel);
        this.appendChildToContentPanel(this.toDate);
    }

    private validate() {
        var fromDate = this.fromDate.getSelectedDateTime(),
            toDate = this.toDate.getSelectedDateTime();

        if (!fromDate) {
            this.fromDate.addClass("invalid");
        } else if (!this.fromDate.isValid()) {
            this.fromDate.addClass("invalid");
        } else {
            this.fromDate.removeClass("invalid");
        }

        if (!toDate) {
            if (this.toDate.isValid()) {
                this.toDate.removeClass("invalid");
            } else {
                this.toDate.addClass("invalid");
            }
        } else if (!this.toDate.isValid()) {
            this.toDate.addClass("invalid");
        } else {
            this.toDate.removeClass("invalid");
        }

        // check toDate is before fromDate
        if (fromDate && this.fromDate.isValid() && toDate && this.toDate.isValid() && toDate < fromDate) {
            this.toDate.addClass("invalid");
        }

        this.confirmScheduleAction.setEnabled(!this.fromDate.hasClass("invalid") && !this.toDate.hasClass("invalid"));
    }

    getFromDate(): Date {
        return this.fromDate.getSelectedDateTime();
    }

    getToDate(): Date {
        return this.toDate.getSelectedDateTime();
    }

    resetPublishDates() {
        this.fromDate.setSelectedDateTime(new Date());
        this.toDate.setSelectedDateTime(null);
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}