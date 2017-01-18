module api.task {

    export class TaskId implements api.Equitable {

        private value: string;

        constructor(value: string) {
            this.value = value;
        }

        public static fromString(str: string): TaskId {
            return new TaskId(str);
        }

        public toString(): string {
            return this.value;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, TaskId)) {
                return false;
            }
            let other = <TaskId>o;
            return this.value === other.value;
        }

        static fromJson(json: TaskIdJson): TaskId {
            return TaskId.fromString(json.taskId);
        }

    }
}
