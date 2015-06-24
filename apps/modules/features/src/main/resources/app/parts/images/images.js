var portal = require('/lib/xp/portal');
var thymeleaf = require('view/thymeleaf');

var scaleOptions = [
    {name: 'Scale Max', value: 'max(600)'},
    {name: 'Scale Wide', value: 'wide(600,200)'},
    {name: 'Scale Block V', value: 'block(600,200)'},
    {name: 'Scale Block H', value: 'block(200,600)'},
    {name: 'Scale Square', value: 'square(600)'},
    {name: 'Scale Width', value: 'width(600)'},
    {name: 'Scale Height', value: 'height(600)'}
];

var filterOptions = [
    {name: '-- No Filter --', value: ''},
    {name: 'Block', value: 'block(10)'},
    {name: 'Blur', value: 'blur(10)'},
    {name: 'Border', value: 'border(2,255)'},
    {name: 'Emboss', value: 'emboss()'},
    {name: 'Gray Scale', value: 'grayscale()'},
    {name: 'Invert', value: 'invert()'},
    {name: 'Rounded', value: 'rounded(10,0,0)'},
    {name: 'Sharpen', value: 'sharpen()'},
    {name: 'RGB Adjust', value: 'rgbadjust(100,0,100)'},
    {name: 'HSB Adjust', value: 'hsbadjust(0,0,0)'},
    {name: 'Edge', value: 'edge()'},
    {name: 'Bump', value: 'bump()'},
    {name: 'Sepia', value: 'sepia(20)'},
    {name: 'Rotate 90', value: 'rotate90()'},
    {name: 'Rotate 180', value: 'rotate180()'},
    {name: 'Rotate 270', value: 'rotate270()'},
    {name: 'Flip horizontal', value: 'fliph()'},
    {name: 'Flip vertical', value: 'flipv()'},
    {name: 'Colorize', value: 'colorize(1,1,1)'},
    {name: 'Colorize HSB', value: 'hsbcolorize(0xFFFFFFFF)'}
];

exports.get = function (req) {
    var imageIds = getImageIds();
    var imageUrls = [];
    for (var i = 0; i < imageIds.length; i++) {
        imageUrls.push(defaultImageUrl(imageIds[i]));
    }

    var postUrl = portal.componentUrl({});
    var params = {
        imageUrls: imageUrls,
        postUrl: postUrl,
        scaleOptions: scaleOptions,
        filterOptions: filterOptions
    };

    var view = resolve('images.html');
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body,
        pageContributions: {
            bodyEnd: [
                '<script src="' + portal.assetUrl({path: 'js/jquery-2.1.4.min.js'}) + '" type="text/javascript"></script>',
                '<script src="' + portal.assetUrl({path: 'js/images-part.js'}) + '" type="text/javascript"></script>'
            ]
        }
    };

};

exports.post = function (req) {
    var filter = req.formParams.filter;
    var scale = req.formParams.scale;

    var imageIds = getImageIds();
    var imageUrls = [];
    for (var i = 0; i < imageIds.length; i++) {
        var imageUrl = portal.imageUrl({
            id: imageIds[i],
            scale: scale,
            filter: filter
        });
        imageUrls.push(imageUrl);
    }

    return {
        contentType: 'application/json',
        body: {
            images: imageUrls
        }
    };
};

function getImageIds() {
    var component = portal.getComponent();

    var imageFolderId = component.config.imageFolder;
    var imageIds = [];
    if (imageFolderId) {
        var result = execute('content.getChildren', {
            key: imageFolderId,
            count: 20
        });
        for (var i = 0; i < result.contents.length; i++) {
            var child = result.contents[i];
            if (child.type === "media:image") {
                imageIds.push(child._id);
            }
        }
    }
    return imageIds;
}

function defaultImageUrl(contentId) {
    return portal.imageUrl({
        id: contentId,
        scale: 'wide(600,400)'
    });
}
