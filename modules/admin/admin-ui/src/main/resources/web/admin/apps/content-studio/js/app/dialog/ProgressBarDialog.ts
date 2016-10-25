import "../../api.ts";
import {DependantItemsDialog} from "../dialog/DependantItemsDialog";
import {ContentPublishMenuManager} from "../browse/ContentPublishMenuManager";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class ProcessingStats {
    // If the content is still being published after this time, show the progress bar (in ms)
    static progressBarDelay: number = 100; // 2000

    // Interval of task polling when publishing the content (in ms)
    static pollInterval: number = 500;     // 500

    // Body class
    static isProcessingClass: string = "is-processing";
}

export class ProgressBarDialog extends DependantItemsDialog {

    // stashes previous checkbox state items, until selected items changed
    // private stash: {[checked: string]: ContentSummaryAndCompareStatus[]} = {};
    // private stashedCount: {[checked: string]: number} = {};
    //
    private progressBar: api.ui.ProgressBar;

    constructor(dialogName: string, dialogSubName: string, dependantsName: string) {
        super(dialogName, dialogSubName, dependantsName);
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
        api.dom.Body.get().addClass(ProcessingStats.isProcessingClass);
        ContentPublishMenuManager.getProgressBar().setValue(0);
        this.addClass(ProcessingStats.isProcessingClass);
        this.hideLoadingSpinner();
        this.progressBar = this.createProgressBar();
    }

    protected disableProgressBar() {
        this.removeClass(ProcessingStats.isProcessingClass);
        api.dom.Body.get().removeClass(ProcessingStats.isProcessingClass);
    }

    protected isProgressBarEnabled() {
        return this.hasClass(ProcessingStats.isProcessingClass);
    }

    protected setProgressValue(value: number) {
        if (this.isProgressBarEnabled()) {
            this.progressBar.setValue(value);
            if (!api.dom.Body.get().isShowingModalDialog()) {
                ContentPublishMenuManager.getProgressBar().setValue(value);
            }
        }
    }

    show() {
        super.show(this.isProgressBarEnabled());
    }

    onPublishComplete() {
        if (this.isProgressBarEnabled()) {
            this.disableProgressBar();
        }

        if (this.isVisible()) {
            this.close();
            return;
        }

        this.hide();
    }

    //
    // private pollPublishTask(taskId: api.task.TaskId, elapsed: number = 0, interval: number = ProcessingStats.pollInterval) {
    //     setTimeout(() => {
    //         if (!this.isProgressBarEnabled() && elapsed >= ProcessingStats.progressBarDelay) {
    //             this.enableProgressBar();
    //         }
    //
    //         new api.task.GetTaskInfoRequest(taskId).sendAndParse().then((task: api.task.TaskInfo) => {
    //             let state = task.getState();
    //             if (!task) {
    //                 return; // task probably expired, stop polling
    //             }
    //
    //             let progress = task.getProgress();
    //
    //             if (state == api.task.TaskState.FINISHED) {
    //                 this.setProgressValue(100);
    //                 this.onPublishComplete();
    //
    //                 api.notify.showSuccess(progress.getInfo());
    //             } else if (state == api.task.TaskState.FAILED) {
    //                 this.onPublishComplete();
    //
    //                 api.notify.showError('Publishing failed: ' + progress.getInfo());
    //             } else {
    //                 this.setProgressValue(task.getProgressPercentage());
    //                 this.pollPublishTask(taskId, elapsed + interval, interval);
    //             }
    //
    //         }).catch((reason: any) => {
    //             this.onPublishComplete();
    //
    //             api.DefaultErrorHandler.handle(reason);
    //         }).done();
    //
    //     }, interval);
    // }
    //
    // private setProgressValue(value: number) {
    //     if (this.isProgressBarEnabled()) {
    //         this.progressBar.setValue(value);
    //         if (!api.dom.Body.get().isShowingModalDialog()) {
    //             ContentPublishMenuManager.getProgressBar().setValue(value);
    //         }
    //     }
    // }
}
