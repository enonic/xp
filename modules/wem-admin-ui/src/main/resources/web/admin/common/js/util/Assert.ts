module api.util {

    export function assert(expression: boolean, message?: string) {
        if (!expression) {
            console.error(message);
            throw (message || "");
        }
    }

    export function assertState(expression: boolean, message?: string) {
        assert(expression, "Illegal state: " + message);
    }

    export function assertNotNull(value: Object, message?: string) {
        assert(value != null, message);
    }

    export function assertNull(value: Object, message?: string) {
        assert(value == null, message);
    }

}
