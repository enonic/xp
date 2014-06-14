module api {

    export enum ExceptionType {
        INFO,
        ERROR,
        WARNING
    }

    export class Exception {

        private message: string;

        private type: ExceptionType;

        constructor(message: string, type: ExceptionType = ExceptionType.ERROR) {
            this.message = message;
            this.type = type;
        }

        getMessage(): string {
            return this.message;
        }

        getType(): ExceptionType {
            return this.type;
        }

    }
}