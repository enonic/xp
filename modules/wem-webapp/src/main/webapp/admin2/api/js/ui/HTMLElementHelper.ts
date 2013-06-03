module API_ui {

    export class HTMLElementHelper {

        static addClass( el:HTMLElement, clsName:string ) {
            if (el.className == '') {
                el.className += clsName;
            }
            else {
                el.className += ' ' + clsName;
            }
        }

    }
}
