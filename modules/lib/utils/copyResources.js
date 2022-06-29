const path = require('path');
const glob = require('glob');
const cpFile = require('cp-file');

const SRC_RESOURCES = 'src/main/resources';
const BUILD_RESOURCES = 'build/resources/main';

glob.sync(`lib-*/${SRC_RESOURCES}/lib/xp/*.ts`).forEach(async (srcResourcePath) => {
  const [libPath] = srcResourcePath.split('/');

  const resourcePath = path.relative(path.join(libPath, SRC_RESOURCES), srcResourcePath);
  const buildResourcePath = path.join(libPath, BUILD_RESOURCES, resourcePath).replaceAll(path.sep, '/');

  await cpFile(srcResourcePath, buildResourcePath);
});