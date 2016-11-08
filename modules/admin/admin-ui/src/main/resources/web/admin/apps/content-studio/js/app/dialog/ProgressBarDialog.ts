import "../../api.ts";
import {DependantItemsDialog} from "../dialog/DependantItemsDialog";
import {MenuButtonProgressBarManager} from "../browse/MenuButtonProgressBarManager";
import TaskState = api.task.TaskState;

export class ProcessingStats {
    // If the content is still being processed after this time, show the progress bar (in ms)
    static progressBarDelay: number = 2000;

    // Interval of task polling when processing the content (in ms)
    static pollInterval: number = 500;
}

export class ProgressBarDialog extends DependantItemsDialog {

    private progressBar: api.ui.ProgressBar;

    private isProcessingClass: string;

    private processHandler: () => void;

    constructor(dialogName: string, dialogSubName: string, dependantsName: string, isProcessingClass: string, processHandler: () => void) {
        super(dialogName, dialogSubName, dependantsName);
        this.isProcessingClass = isProcessingClass;
        this.processHandler = processHandler;
    }

    protected createProgressBar() {
        if (this.progressBar) {
            this.progressBar.setValue(0);
            return this.progressBar;
        }

        let progressBar = new api.ui.ProgressBar(0);
        this.appendChildToContentPanel(progressBar);

        return progressBar;
    }

    protected enableProgressBar() {
        this.addClass(this.isProcessingClass);
        api.dom.Body.get().addClass(this.isProcessingClass);

        MenuButtonProgressBarManager.getProgressBar().setValue(0);
        this.hideLoadingSpinner();
        this.progressBar = this.createProgressBar();
        MenuButtonProgressBarManager.updateProgressHandler(this.processHandler);
    }

    protected disableProgressBar() {
        this.removeClass(this.isProcessingClass);
        api.dom.Body.get().removeClass(this.isProcessingClass);
    }

    protected isProgressBarEnabled() {
        return this.hasClass(this.isProcessingClass);
    }

    protected setProgressValue(value: number) {
        if (this.isProgressBarEnabled()) {
            this.progressBar.setValue(value);
            if (!api.dom.Body.get().isShowingModalDialog()) {
                MenuButtonProgressBarManager.getProgressBar().setValue(value);
            }
        }
    }

    show() {
        super.show(this.isProgressBarEnabled());
    }

    onProcessingComplete() {
        if (this.isProgressBarEnabled()) {
            this.disableProgressBar();
        }

        if (this.isVisible()) {
            this.close();
            return;
        }

        this.hide();
    }

    protected pollTask(taskId: api.task.TaskId, elapsed: number = 0) {
        const interval = ProcessingStats.pollInterval;
        setTimeout(() => {
            if (!this.isProgressBarEnabled() && elapsed >= ProcessingStats.progressBarDelay) {
                this.enableProgressBar();
            }

            new api.task.GetTaskInfoRequest(taskId).sendAndParse().then((task: api.task.TaskInfo) => {
                let state = task.getState();
                if (!task) {
                    return; // task probably expired, stop polling
                }

                let progress = task.getProgress();

                switch (state) {
                case TaskState.FINISHED:
                    this.setProgressValue(100);
                    this.onProcessingComplete();
                    api.notify.showSuccess(progress.getInfo());
                    break;
                case TaskState.FAILED:
                    this.onProcessingComplete();
                    api.notify.showError('Processing failed: ' + progress.getInfo());
                    break;
                default:
                    this.setProgressValue(task.getProgressPercentage());
                    this.pollTask(taskId, elapsed + interval);
                }
            }).catch((reason: any) => {
                this.onProcessingComplete();

                api.DefaultErrorHandler.handle(reason);
            }).done();

        }, interval);
    }
}
