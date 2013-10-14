module api_app_browse_grid {

    export class DateTimeFormatter {

        static format(row:number, cell:number, value:any, columnDef:any, item:any):string {
            return DateTimeFormatter.createHtml(value);
        }

        static createHtml(date:Date):string {

            var s = "";
            s += DateTimeFormatter.zeroPad(date.getFullYear(), 4);
            s += "-";
            s += DateTimeFormatter.zeroPad(date.getMonth() + 1, 2);
            s += "-";
            s += DateTimeFormatter.zeroPad(date.getDate(), 2);
            s += " ";
            s += DateTimeFormatter.zeroPad(date.getHours(), 2);
            s += ":";
            s += DateTimeFormatter.zeroPad(date.getMinutes(), 2);
            s += ":";
            s += DateTimeFormatter.zeroPad(date.getSeconds(), 2);
            return s;
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