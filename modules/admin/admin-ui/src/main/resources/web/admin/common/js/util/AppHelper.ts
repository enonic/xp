module api.util {

    export class AppHelper {

        // Returns a function, that, as long as it continues to be invoked, will not
        // be triggered. The function will be called after it stops being called for
        // N milliseconds. If `immediate` is passed, trigger the function on the
        // leading edge, instead of the trailing.
        static debounce(func, wait, immediate) {
            var timeout;
            return function (...args: any[]) {
                var context = this, args = arguments;
                var later = function () {
                    timeout = null;
                    if (!immediate) {
                        func.apply(context, args);
                    }
                };
                var callNow = immediate && !timeout;
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
                if (callNow) {
                    func.apply(context, args);
                }
            };
        }

        static preventDragRedirect(message: String = "", element?: api.dom.Element) {
            element = element || api.dom.Body.get();

            var window = api.dom.WindowDOM.get();
            var timeout = null;

            var beforeUnloadHandler = (event) => {
                (event || window.asWindow().event)['returnValue'] = message;
                event.preventDefault();
                return message;
            };

            var unBeforeUnload = () => {
                timeout = null;
                window.unBeforeUnload(beforeUnloadHandler);
            };

            element.onDragOver(() => {
                if (!timeout) {
                    window.onBeforeUnload(beforeUnloadHandler);
                }
                clearTimeout(timeout);
                timeout = setTimeout(unBeforeUnload, 100);
            });
        }
    }

}