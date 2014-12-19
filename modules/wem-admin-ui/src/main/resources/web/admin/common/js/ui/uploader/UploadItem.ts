module api.ui.uploader {

    export class UploadItem<MODEL> {

        private file: PluploadFile;
        private model: MODEL;

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
            return this;
        }

        getStatus(): PluploadStatus {
            return this.file.status;
        }

        setStatus(status: PluploadStatus): UploadItem<MODEL> {
            this.file.status = status;
            return this;
        }

    }
}