module api.util {

    export class AppHelper {

        // Returns a function, that, as long as it continues to be invoked, will not
        // be triggered. The function will be called after it stops being called for
        // N milliseconds. If `immediate` is passed, trigger the function on the
        // leading edge, instead of the trailing.
        static debounce(func: Function, wait: number, immediate: boolean): (...args: any[]) => void {
            let timeout;
            return function (...anyArgs: any[]) {
                const context = this;
                const args = arguments;
                const later = function () {
                    timeout = null;
                    if (!immediate) {
                        func.apply(context, args);
                    }
                };
                const callNow = immediate && !timeout;
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
                if (callNow) {
                    func.apply(context, args);
                }
            };
        }

        // Handles the result of the initialization, while the result is truthy
        static whileTruthy(initializer: () => any, callback: (value: any) => void): void {
            let result: any;

            for (result = initializer(); !!result; result = initializer()) {
                callback(result);
            }
        }

        static preventDragRedirect(message: String = '', element?: api.dom.Element): void {
            element = element || api.dom.Body.get();

            let window = api.dom.WindowDOM.get();
            let timeout = null;

            let beforeUnloadHandler = (event) => {
                (event || window.asWindow().event)['returnValue'] = message;
                event.preventDefault();
                return message;
            };

            let unBeforeUnload = () => {
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

        static dispatchCustomEvent(name: string, element: api.dom.Element): void {
            wemjq(element.getHTMLElement()).trigger(name);
        }

        static focusInOut(element: api.dom.Element, onFocusOut: () => void, wait: number = 50, preventMouseDown: boolean = true): void {
            let focusOutTimeout = 0;
            let target;

            element.onFocusOut((event) => {
                if(target == event.target) {
                    focusOutTimeout = setTimeout(onFocusOut, wait);
                }
            });

            element.onFocusIn((event) => {
                target = event.target;
                clearTimeout(focusOutTimeout);
            });

            // Prevent focus loss on mouse down
            if (preventMouseDown) {
                element.onMouseDown((e) => {
                    // if click is inside of input then focus will remain in it and no need to prevent default
                    if ((<HTMLElement>e.target).tagName.toLowerCase() !== 'input') {
                        e.preventDefault();
                    }
                });
            }
        }

        static lockEvent(event: Event): void {
            event.stopPropagation();
            event.preventDefault();
        }
    }

}
