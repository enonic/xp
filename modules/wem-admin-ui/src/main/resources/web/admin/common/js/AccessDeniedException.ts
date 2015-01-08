module api {

    export class AccessDeniedException extends Exception {

        constructor(message: string) {
            super(message, ExceptionType.WARNING);
        }

    }
}