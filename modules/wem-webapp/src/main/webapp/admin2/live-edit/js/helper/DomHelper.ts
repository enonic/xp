interface DocumentSize {
    width: number;
    height: number;
}

interface ViewPortSize {
    width: number;
    height: number;
}


module liveedit {
    export class DomHelper {

        static $ = $liveedit;

        static getDocumentSize():DocumentSize {
            var $document = $(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        }


        static getViewPortSize():ViewPortSize {
            var $window = $(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        }


        static getDocumentScrollTop():number {
            return $(document).scrollTop();
        }

    }
}