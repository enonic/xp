module api_util {

    export class CookieHelper {

        static setCookie(name:string, value:string, days:number = 1):void {
            if (days) {
                var date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                var expires = '; expires=' + date.toUTCString();
            } else {
                var expires = '';
            }
            document.cookie = CookieHelper.escape(name) + '=' + CookieHelper.escape(value) + expires + '; path=/';
        }

        static getCookie(name:string):string {
            var nameEQ = CookieHelper.escape(name) + '=';
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
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
