module api.ui.treegrid {

    export class DateTimeFormatter {

        static format(row:number, cell:number, value:any, columnDef:any, item:any): string {
            return DateTimeFormatter.createHtml(value);
        }

        static formatNoTimestamp(row:number, cell:number, value:any, columnDef:any, item:any): string {
            return DateTimeFormatter.createHtmlNoTimestamp(value);
        }

        static createHtml(date:Date): string {
            if (!!date) {
                var s = DateTimeFormatter.zeroPad(date.getFullYear(), 4) +
                    "-" +
                    DateTimeFormatter.zeroPad(date.getMonth() + 1, 2) +
                    "-" +
                    DateTimeFormatter.zeroPad(date.getDate(), 2)+
                    " " +
                    DateTimeFormatter.zeroPad(date.getHours(), 2)+
                    ":" +
                    DateTimeFormatter.zeroPad(date.getMinutes(), 2) +
                    ":" +
                    DateTimeFormatter.zeroPad(date.getSeconds(), 2);
                return s;
            }

            return "";
        }

        static createHtmlNoTimestamp(date:Date):string {
            if (!!date) {
                var s = DateTimeFormatter.zeroPad(date.getFullYear(), 4) +
                    "-" +
                    DateTimeFormatter.zeroPad(date.getMonth() + 1, 2) +
                    "-" +
                    DateTimeFormatter.zeroPad(date.getDate(), 2);
                return s;
            }

            return "";
        }

        private static zeroPad(n:number, width:number) {
            var nWidth = n.toString().length;
            if (nWidth >= width) {
                return "" + n;
            }
            var neededZeroes = width - nWidth;
            var s = "";
            for( var i = 0; i < neededZeroes; i++ ) {
                s += "0";
            }

            return s + n
        }
    }
}