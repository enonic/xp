import path from "path";

export default (src, dest, root) => commonPaths(src, dest, root, root);

export function commonPaths(src, dest, srcRoot, destRoot) {
    const fullSrc = path.join(srcRoot, src);
    const fullDest = path.join(destRoot, dest);

    return {
        srcRoot,
        destRoot,
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
}

export function anyPath(root, ext = '*') {
    return `${root}${path.sep}**${path.sep}*.${ext}`;
}
