module api.task {

    export class TaskProgress {

        private info: string;
        private current: number;
        private total: number;

        constructor(builder: TaskProgressBuilder) {
            this.info = builder.info;
            this.current = builder.current;
            this.total = builder.total;
        }

        getInfo(): string {
            return this.info;
        }

        getCurrent(): number {
            return this.current;
        }

        getTotal(): number {
            return this.total;
        }

        static create(): TaskProgressBuilder {
            return new TaskProgressBuilder();
        }
    }

    export class TaskProgressBuilder {

        info: string;
        current: number;
        total: number;

        fromSource(source: TaskProgress): TaskProgressBuilder {
            this.info = source.getInfo();
            this.current = source.getCurrent();
            this.total = source.getTotal();
            return this;
        }

        fromJson(json: api.task.TaskProgressJson): TaskProgressBuilder {
            this.info = json.info;
            this.current = json.current;
            this.total = json.total;
            return this;
        }

        setInfo(info: string): TaskProgressBuilder {
            this.info = info;
            return this;
        }

        setCurrent(current: number): TaskProgressBuilder {
            this.current = current;
            return this;
        }

        setTotal(total: number): TaskProgressBuilder {
            this.total = total;
            return this;
        }

        build(): TaskProgress {
            return new TaskProgress(this);
        }
    }
}