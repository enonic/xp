module api.notify {

    import Type = api.notify.Type;

    export class Exception {

        private message: string;

        private type: Type;

        constructor(message: string, type: Type = Type.ERROR) {
            this.message = message;
            this.type = type;
        }

        getMessage(): string {
            return this.message;
        }

        getType(): Type {
            return this.type;
        }

    }
}