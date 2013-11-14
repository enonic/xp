module api_util {

    export function assert(expression:boolean, message?:string) {
        console.assert(expression, message);
    }

    export function assertNotNull(value:Object, message?:string) {
        assert(value != null, message);
    }

}
