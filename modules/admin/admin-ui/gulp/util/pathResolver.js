import path from "path";

export default (root, src, dest) => {
    const fullSrc = path.join(root, src);
    const fullDest = path.join(root, dest);

    return {
        root,
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

export const anyPath = (root, ext = '*') => {
    return `${root}${path.sep}**${path.sep}*.${ext}`;
};
