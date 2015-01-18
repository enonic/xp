interface DocumentSize {
    width: number;
    height: number;
}

interface ViewPortSize {
    width: number;
    height: number;
}


module LiveEdit {

    export class DomHelper {


        public static getDocumentSize():DocumentSize {
            return {
                width: wemjq(document).width(),
                height: wemjq(document).height()
            };
        }

        public static getViewPortSize():ViewPortSize {
            var win:JQuery = wemjq(window);
            return {
                width: win.width(),
                height: win.height()
            };
        }

        public static getDocumentScrollTop():number {
            return wemjq(document).scrollTop();
        }

        public static supportsTouch():Boolean {
            return document.hasOwnProperty('ontouchend');
        }

    }
}