var path = require("path");

module.exports.commonPaths = function (src, dest, srcRoot, destRoot) {
    destRoot = destRoot || srcRoot;
    var fullSrc = path.join(srcRoot, src);
    var fullDest = path.join(destRoot, dest);

    return {
        srcRoot: srcRoot,
        destRoot: destRoot,
        src: {
            full: fullSrc,
            dir: path.dirname(fullSrc),
            base: path.basename(fullSrc)
        },
        dest: {
            full: fullDest,
            dir: path.dirname(fullDest),
            base: path.basename(fullDest)
        }
    }
};

module.exports.anyPath = function (root, ext) {
    return root + path.sep + "**" + path.sep + "*." + (ext || "*");
};
