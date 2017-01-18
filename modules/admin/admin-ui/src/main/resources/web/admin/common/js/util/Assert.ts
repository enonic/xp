module api.util {

    export function assert(expression: boolean, message?: string) {
        if (!expression) {
            console.error(message);
            throw new Error(message || 'Assertion failed');
        }
    }

    export function assertState(expression: boolean, message?: string) {
        assert(expression, 'Illegal state: ' + message);
    }

    export function assertNotNull<T>(value: T, message?: string): T {
        assert(value != null, message || 'Value may not be null');
        return value;
    }

    export function assertNull(value: Object, message?: string) {
        assert(value == null, message);
    }

}
