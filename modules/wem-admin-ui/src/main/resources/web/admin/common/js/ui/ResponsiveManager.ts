module api.ui {

    export class ResponsiveManager {

        private static window = api.dom.Window.get();

        private static responsiveListeners: ResponsiveListener[] = [];

        // Custom handler will be executed in addition on element update
        static onAvailableSizeChanged(el: api.dom.Element, handler:Function = () => {}) {
            var responsiveItem:ResponsiveItem = new ResponsiveItem(el, handler),
                listener = () => { responsiveItem.update(); },
                responsiveListener = new ResponsiveListener(responsiveItem, listener);

            ResponsiveManager.responsiveListeners.push(responsiveListener);

            ResponsiveManager.window.getHTMLElement().addEventListener('availablesizechange', listener);
            ResponsiveManager.window.onResized(listener);
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

        // Manual event triggering
        static fireResizeEvent() {
            var customEvent = document.createEvent('Event');
            customEvent.initEvent('availablesizechange', false, true); // No bubbling
            ResponsiveManager.window.getHTMLElement().dispatchEvent(customEvent);
        }
    }
}
