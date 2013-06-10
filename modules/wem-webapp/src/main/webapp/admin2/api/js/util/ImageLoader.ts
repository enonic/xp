module api_util {
    export class ImageLoader {

        private static images:HTMLImageElement[] = [];

        static get(url:string, width?:number, height?:number) {
            var imageFound:bool = false;
            var returnImage;
            for (var i in images) {
                if (images[i].src == url) {
                    imageFound = true;
                    returnImage = images[i];
                }
            }

            if (!imageFound) {
                var image = new Image(width, height);
                image.src = url;
                //image.height = height;
                //image.width = width;
                images[images.length + 1] = image;
                returnImage = image;
            }

            return returnImage;
        }
    }

}