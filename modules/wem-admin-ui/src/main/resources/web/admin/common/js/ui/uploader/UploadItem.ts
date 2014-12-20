module api.ui.uploader {

    export class UploadItem<MODEL> {

        private file: PluploadFile;
        private model: MODEL;

        private failedListeners: {(): void}[] = [];
        private uploadListeners: {(model: MODEL): void}[] = [];
        private progressListeners: {(progress: number): void}[] = [];

        constructor(file: PluploadFile) {
            this.file = file;
        }

        getId(): string {
            return this.file.id;
        }

        setId(id: string): UploadItem<MODEL> {
            this.file.id = id;
            return this;
        }

        getModel(): MODEL {
            return this.model;
        }

        setModel(model: MODEL): UploadItem<MODEL> {
            this.model = model;
            if (model) {
                this.notifyUploaded(model);
            } else {
                this.notifyFailed();
            }
            return this;
        }

        getName(): string {
            return this.file.name;
        }

        setName(name: string): UploadItem<MODEL> {
            this.file.name = name;
            return this;
        }

        getMimeType(): string {
            return this.file.type;
        }

        setMimeType(type: string): UploadItem<MODEL> {
            this.file.type = type;
            return this;
        }

        getSize(): number {
            return this.file.size;
        }

        setSize(size: number): UploadItem<MODEL> {
            this.file.size = size;
            return this;
        }

        getProgress(): number {
            return this.file.percent;
        }

        setProgress(progress: number): UploadItem<MODEL> {
            this.file.percent = progress;
            this.notifyProgress(progress);
            return this;
        }

        getStatus(): PluploadStatus {
            return this.file.status;
        }

        setStatus(status: PluploadStatus): UploadItem<MODEL> {
            this.file.status = status;
            return this;
        }

        isUploaded(): boolean {
            return !!this.model
        }

        onProgress(listener: (progress: number) => void) {
            this.progressListeners.push(listener);
        }

        unProgress(listener: (progress: number) => void) {
            this.progressListeners = this.progressListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyProgress(progress: number) {
            this.progressListeners.forEach((listener) => {
                listener(progress);
            })
        }

        onUploaded(listener: (model: MODEL) => void) {
            this.uploadListeners.push(listener);
        }

        unUploaded(listener: (model: MODEL) => void) {
            this.uploadListeners = this.uploadListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyUploaded(model: MODEL) {
            this.uploadListeners.forEach((listener) => {
                listener(model);
            })
        }

        onFailed(listener: () => void) {
            this.failedListeners.push(listener);
        }

        unFailed(listener: () => void) {
            this.failedListeners = this.failedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyFailed() {
            this.failedListeners.forEach((listener) => {
                listener();
            })
        }

    }
}