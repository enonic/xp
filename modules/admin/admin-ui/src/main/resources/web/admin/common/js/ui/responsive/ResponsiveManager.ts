module api.ui.responsive {

    export class ResponsiveManager {

        private static window = api.dom.WindowDOM.get();

        private static responsiveListeners: ResponsiveListener[] = [];

        // Custom handler will be executed in addition on element update
        static onAvailableSizeChanged(el: api.dom.Element, handler: (item: ResponsiveItem) => void = (item: ResponsiveItem) => {
        }): ResponsiveItem {
            var responsiveItem: ResponsiveItem = new ResponsiveItem(el, handler),
                listener = () => {
                    responsiveItem.update();
                },
                responsiveListener = new ResponsiveListener(responsiveItem, listener);

            ResponsiveManager.responsiveListeners.push(responsiveListener);

            ResponsiveManager.window.getHTMLElement().addEventListener('availablesizechange', listener);
            ResponsiveManager.window.onResized(listener);

            return responsiveItem;
        }

        static unAvailableSizeChanged(el: api.dom.Element) {

            ResponsiveManager.responsiveListeners =
            ResponsiveManager.responsiveListeners.filter((curr) => {
                if (curr.getItem().getElement() === el) {
                    ResponsiveManager.window.getHTMLElement().removeEventListener('availablesizechange', curr.getListener());
                    ResponsiveManager.window.unResized(curr.getListener());
                    return false;
                } else {
                    return true;
                }
            });
        }

        static unAvailableSizeChangedByItem(item: ResponsiveItem) {

            ResponsiveManager.responsiveListeners =
            ResponsiveManager.responsiveListeners.filter((curr) => {
                if (curr.getItem() === item) {
                    ResponsiveManager.window.getHTMLElement().removeEventListener('availablesizechange', curr.getListener());
                    ResponsiveManager.window.unResized(curr.getListener());
                    return false;
                } else {
                    return true;
                }
            });
        }

        // Manual event triggering
        static fireResizeEvent() {
            var customEvent = document.createEvent('Event');
            customEvent.initEvent('availablesizechange', false, true); // No bubbling
            ResponsiveManager.window.getHTMLElement().dispatchEvent(customEvent);
        }

        static getWindow(): api.dom.WindowDOM {
            return ResponsiveManager.window;
        }
    }

    ResponsiveManager.onAvailableSizeChanged(api.dom.Body.get());
}
