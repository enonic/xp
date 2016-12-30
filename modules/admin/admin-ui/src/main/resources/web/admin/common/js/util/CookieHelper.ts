module api.util {

    export class CookieHelper {

        static setCookie(name:string, value:string, days:number = 1):void {
            let expires;
            if (days) {
                const date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                expires = '; expires=' + date.toUTCString();
            } else {
                expires = '';
            }
            document.cookie = CookieHelper.escape(name) + '=' + CookieHelper.escape(value) + expires + '; path=/';
        }

        static getCookie(name:string):string {
            let nameEQ = CookieHelper.escape(name) + '=';
            let ca = document.cookie.split(';');
            for (let i = 0; i < ca.length; i++) {
                let c = ca[i];
                while (c.charAt(0) === ' ') {
                    c = c.substring(1, c.length);
                }
                if (c.indexOf(nameEQ) === 0) {
                    return CookieHelper.unescape(c.substring(nameEQ.length, c.length));
                }
            }
            return null;
        }

        static removeCookie(name:string):void {
            CookieHelper.setCookie(name, '', -1);
        }

        private static escape(value:string):string {
            return encodeURIComponent(value);
        }

        private static unescape(value:string):string {
            return decodeURIComponent(value);
        }
    }
}
