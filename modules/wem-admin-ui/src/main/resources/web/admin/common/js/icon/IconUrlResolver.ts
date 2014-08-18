module api.icon {

    export class IconUrlResolver {

        appendParam(paramName: string, paramValue: string, url: string) {
            var questionIndex = url.indexOf("?");
            if (questionIndex == -1) {
                url += "?" + paramName + "=" + paramValue;
                return url;
            }
            else if (url.charAt(url.length - 1) == '?') {
                url += paramName + "=" + paramValue;
                return url;
            }
            else {
                url += "&" + paramName + "=" + paramValue;
                return url;
            }
        }
    }
}