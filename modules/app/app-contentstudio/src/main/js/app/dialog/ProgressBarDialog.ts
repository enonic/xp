import '../../api.ts';
import {DependantItemsDialog} from '../dialog/DependantItemsDialog';
import {MenuButtonProgressBarManager} from '../browse/MenuButtonProgressBarManager';
import TaskState = api.task.TaskState;
import ModalDialogButtonRow = api.ui.dialog.ButtonRow;
import i18n = api.util.i18n;

export class ProcessingStats {
    // If the content is still being processed after this time, show the progress bar (in ms)
    static progressBarDelay: number = 200;

    // Interval of task polling when processing the content (in ms)
    static pollInterval: number = 500;
}

export interface ProgressBarConfig {
    dialogName: string;
    dialogSubName: string;
    dependantsName: string;
    isProcessingClass: string;
    processHandler: () => void;
    buttonRow?: ModalDialogButtonRow;
}

export class ProgressBarDialog extends DependantItemsDialog {

    private progressBar: api.ui.ProgressBar;

    private isProcessingClass: string;

    private processHandler: () => void;

    private progressCompleteListeners: ((taskState: TaskState) => void)[] = [];

    constructor(config: ProgressBarConfig) {
        super(config.dialogName, config.dialogSubName, config.dependantsName, config.buttonRow);
        this.isProcessingClass = config.isProcessingClass;
        this.processHandler = config.processHandler;
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
        this.unlockControls();
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

    handleProcessingComplete() {
        if (this.isProgressBarEnabled()) {
            this.disableProgressBar();
        }

        if (this.isVisible()) {
            this.close();
        }
    }

    protected handleSucceeded() {
        this.setProgressValue(100);
        this.handleProcessingComplete();
    }

    protected handleFailed() {
        this.handleProcessingComplete();
    }

    onProgressComplete(listener: (taskState: TaskState) => void) {
        this.progressCompleteListeners.push(listener);
    }

    unProgressComplete(listener: (taskState: TaskState) => void) {
        this.progressCompleteListeners = this.progressCompleteListeners.filter(function (curr: (taskState: TaskState) => void) {
            return curr !== listener;
        });
    }

    private notifyProgressComplete(taskState: TaskState) {
        this.progressCompleteListeners.forEach((listener) => {
            listener(taskState);
        });
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

                const progress = task.getProgress();

                switch (state) {
                case TaskState.FINISHED:
                    this.handleSucceeded();
                    api.notify.showSuccess(progress.getInfo());
                    this.notifyProgressComplete(TaskState.FINISHED);
                    break;
                case TaskState.FAILED:
                    this.handleFailed();
                    api.notify.showError(i18n('notify.process.failed', progress.getInfo()));
                    this.notifyProgressComplete(TaskState.FAILED);
                    break;
                default:
                    this.setProgressValue(task.getProgressPercentage());
                    this.pollTask(taskId, elapsed + interval);
                }
            }).catch((reason: any) => {
                this.handleProcessingComplete();

                api.DefaultErrorHandler.handle(reason);
            }).done();

        }, interval);
    }
}
