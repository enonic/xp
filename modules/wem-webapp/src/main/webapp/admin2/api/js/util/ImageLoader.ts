module api_util {
    export class ImageLoader {

        private static cachedImages:HTMLImageElement[] = [];

        static get(url:string, width?:number, height?:number):HTMLImageElement {
            var imageFound:boolean = false;
            var returnImage:HTMLImageElement;
            url = encodeURI(url);

            for (var i in ImageLoader.cachedImages) {
                if (ImageLoader.cachedImages[i].src == url) {
                    imageFound = true;
                    returnImage = ImageLoader.cachedImages[i];
                }
            }

            if (!imageFound) {
                var image:HTMLImageElement = new Image(width, height);
                image.src = url;
                //image.height = height;
                //image.width = width;
                ImageLoader.cachedImages[ImageLoader.cachedImages.length + 1] = image;
                returnImage = image;
            }

            return returnImage;
        }
    }

}