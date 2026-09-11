# Predefined image processing styles

WebP and AVIF output requires a predefined style and uses bundled ImageMagick.
No separate ImageMagick installation or runtime download is required. Existing JPEG/PNG/GIF
processing continues to use ImageIO.

Define each permitted variant in the application's `cms/style/style.yml`:

```yaml
kind: Style
styles:
  - type: Image
    name: card
    label: Card
    scale: block(640,360)
    quality: 80
```

Generate the URL with:

```javascript
portal.imageUrl({ id: imageId, style: 'com.example.site:card', format: 'webp' });
portal.imageUrl({ id: imageId, style: 'com.example.site:card', format: 'avif' });
```

The style does not contain an output format. Choose it separately using the
existing `format` argument to `portal.imageUrl`. The URL uses the `full` path
placeholder, the requested output extension (for example `photo.jpg.webp`), and
`?style=com.example.site%3Acard`. The output format is selected by the extension.
Both the legacy image endpoint and `media:image` support this form.

A processing style must define `scale`. Supported scales are
`max`, `width`, `height`, `square`, `block`, and `wide`, with fixed positive
dimensions and the existing `scale.maxDimension` limit. `full` is deliberately
excluded from processing styles. Formats are `jpeg`, `png`, `gif`, `webp`, and
`avif`. Optional `quality` defaults to 85; optional `filter` uses the existing
filter syntax and limits. `background` is a hexadecimal RGB value used when
flattening to JPEG/GIF; PNG/WebP/AVIF preserve transparency. The existing
`aspectRatio` field remains an editor hint; the processing `scale` explicitly
defines the output geometry. Define separate styles for responsive sizes.

Style URLs reject scale, quality, filter, and background overrides, including
empty query parameters and duplicate styles. Select format with the API's
`format` argument, which is encoded as the output extension; a raw `format`
query parameter is not supported. WebP/AVIF conversion without a style is rejected
both when generating URLs and when serving requests.
The image service resolves styles again before processing, so arbitrary URL
parameters cannot authorize a modern-format conversion. Content permissions,
stored cropping, focal point, and orientation still apply.

Configure `com.enonic.xp.image.cfg`:

```properties
encoding.enabled = true
encoding.maxConcurrent = 2
encoding.maxQueue = 8
encoding.queueTimeoutSeconds = 5
encoding.timeoutSeconds = 30
encoding.maxPixels = 40000000
```

The build downloads checksum-pinned upstream portable distributions and embeds
both the executable and its codec libraries in the image bundle. On first use,
XP selects the platform resource and extracts it into a private directory under
`java.io.tmpdir`. That directory must allow execution. The extracted distribution
is reused for the lifetime of the bundle and removed on JVM shutdown. Linux
extraction does not require FUSE. This follows the platform-resource/extraction
approach used by native-library loaders such as Brotli4j; conversion runs in an
isolated process so timeouts can terminate native code.

Bundled platforms currently cover Linux x86-64 and Windows x86-64/ARM64 using
ImageMagick 7.1.2-31. Other platforms report an unavailable encoder for modern
output; existing ImageIO processing remains available. The complete upstream
archives retain their licenses and dependencies. Version and SHA-256 pins live
in `native/distributions.json`. Building XP requires 7-Zip to repack upstream
Windows archives as ZIP; production servers do not need it. Install `p7zip-full`
on Linux or 7-Zip on Windows. Use `-PimageMagickSevenZip=/path/to/7z` to select
a build-time extractor (for example Homebrew's `7zz` on macOS).
Adding another platform requires a portable
upstream distribution and a native encoding test on that platform.

No process is started for ordinary formats or modern-format cache hits.
Unstyled original WebP/AVIF/SVG files retain their existing
pass-through behavior. Styled processing currently accepts formats decoded by
the existing ImageIO backend; WebP/AVIF/SVG inputs are rejected, and GIF styles
process the first frame.

Modern cache misses have bounded concurrency and a bounded waiting queue.
Capacity exhaustion and queue timeout return HTTP 429. Source and output pixel
counts are checked, and the existing heap memory estimate is a hard admission
limit for modern conversions. The external encoder receives only a generated
PNG and fixed arguments, uses one configured ImageMagick thread, and is killed
on timeout or interruption. Temporary files and failed cache entries are
removed. ImageMagick pixel-cache memory/map/disk limits are also set; these are
not an operating-system limit on all memory allocated by codec libraries.

The cache key includes the source checksum and resolved processing parameters.
Concurrent requests recheck the cache after obtaining the file lock, preventing
duplicate conversions. The URL fingerprint also includes the style's scale,
quality, filter, and background, so editing those fields produces a new
URL. Different output formats have distinct URL extensions and disk cache keys. The handler checks the same fingerprint before applying the configured
public/private immutable cache header. Old fingerprints do not receive immutable
cache headers after a style changes. A style change during request processing
is rejected before encoding, preventing a response with a mismatched fingerprint.

Tests cover style fingerprints and enforcement, cache coalescing, encoder failure
and timeout cleanup, and actual WebP/AVIF output using the embedded distribution.
