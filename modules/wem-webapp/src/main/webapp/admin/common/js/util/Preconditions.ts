module api_util {

    export function assert(expression:boolean, message?:string) {
        if (!expression) {
            var realMessage = "Assert failed" + (message != null ? (': ' + message) : '');
            throw new Error(realMessage);
        }
    }

    export function assertNotNull(value:Object, message?:string) {
        assert(value != null, message);
    }

}
