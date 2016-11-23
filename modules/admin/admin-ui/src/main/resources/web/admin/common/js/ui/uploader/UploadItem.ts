module api.ui.uploader {

    export class UploadItem<MODEL extends api.Equitable> implements api.Equitable {

        private file: FineUploaderFile;
        private model: MODEL;
        private fileName: string;

        private failedListeners: {(): void}[] = [];
        private uploadStoppedListeners: {(): void}[] = [];
        private uploadListeners: {(model: MODEL): void}[] = [];
        private progressListeners: {(progress: number): void}[] = [];

        constructor(file: FineUploaderFile) {
            this.file = file;
            this.fileName = file.name;
        }

        getId(): string {
            return this.file.id + "";
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
            return this.fileName;
        }

        setName(name: string): UploadItem<MODEL> {
            this.fileName = name;
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

        getStatus(): string {
            return this.file.status;
        }

        setStatus(status: string): UploadItem<MODEL> {
            this.file.status = status;
            return this;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UploadItem)) {
                return false;
            }

            var other = <UploadItem<MODEL>>o;

            if (!api.ObjectHelper.equals(this.model, other.model)) {
                return false;
            }

            if (this.file && other.file) {

                if (!api.ObjectHelper.stringEquals(this.file.id, other.file.id) ||
                    !api.ObjectHelper.stringEquals(this.fileName, other.fileName) ||
                    !api.ObjectHelper.numberEquals(this.file.percent, other.file.percent) ||
                    //!api.ObjectHelper.stringEquals(this.file.type, other.file.type) ||
                    !api.ObjectHelper.numberEquals(this.file.size, other.file.size) ||
                    //!api.ObjectHelper.numberEquals(this.file.origSize, other.file.origSize) ||
                    this.file.status != this.file.status) {
                    return false;
                }

                /*if (this.file.lastModifiedDate.getMilliseconds() != other.file.lastModifiedDate.getMilliseconds()) {
                    return false;
                 }*/

            } else if (!this.file && !other.file) {
                return true;
            } else {
                return false;
            }

            return true;

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

        notifyFailed() {
            this.failedListeners.forEach((listener) => {
                listener();
            })
        }

        onUploadStopped(listener: () => void) {
            this.uploadStoppedListeners.push(listener);
        }

        unUploadStopped(listener: () => void) {
            this.uploadStoppedListeners = this.uploadStoppedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        notifyUploadStopped() {
            this.uploadStoppedListeners.forEach((listener) => {
                listener();
            })
        }

    }
}