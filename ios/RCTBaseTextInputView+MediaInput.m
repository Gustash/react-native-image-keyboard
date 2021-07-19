#import "RCTBaseTextInputView+MediaInput.h"
#import "NSData+mimeTypeByGuessingFromData.h"
#include <objc/runtime.h>
@import MobileCoreServices;

@implementation RCTBaseTextInputView (MediaInput)
static char key;
static NSArray *acceptedTypes;

+ (void)initialize {
    acceptedTypes = @[(NSString *)kUTTypePNG,
                      (NSString *)kUTTypeGIF,
                      (NSString *)kUTTypeJPEG];
}

- (void)setOnImageChange:(RCTDirectEventBlock)onImageChange
{
    objc_setAssociatedObject(self, &key, onImageChange, OBJC_ASSOCIATION_RETAIN);
}

- (RCTDirectEventBlock)onImageChange
{
    return objc_getAssociatedObject(self, &key);
}

- (BOOL)pasteboardHasImages
{
    if (@available(iOS 10.0, *)) {
        return [UIPasteboard generalPasteboard].hasImages;
    } else {
        return (BOOL)[self extractImageFromPasteboard];
    }
}

- (NSData *)extractImageFromPasteboard
{
    UIPasteboard *generalPasteboard = [UIPasteboard generalPasteboard];

    for (NSString* type in acceptedTypes) {
        NSData *data = [generalPasteboard dataForPasteboardType:type];

        if (data) {
            return data;
        }
    }

    return nil;
}

- (void)paste:(id)sender
{
    if (!self.onImageChange) {
        [super paste:sender];
        return;
    }

    BOOL hasImage;
    NSData *image;
    if (@available(iOS 10.0, *)) {
        hasImage = [UIPasteboard generalPasteboard].hasImages;
    } else {
        image = [self extractImageFromPasteboard];
        hasImage = (BOOL) image;
    }

    if (hasImage) {
        if (@available(iOS 10.0, *)) {
            image = [self extractImageFromPasteboard];
        }
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            NSString *mimeType = [image mimeTypeByGuessingFromData];
            NSString *fileExtension = [mimeType stringByReplacingOccurrencesOfString:@"image/"
                                                                          withString:@""];

            NSString *base64 = [image base64EncodedStringWithOptions:0];

            NSArray<NSString*> *paths = NSSearchPathForDirectoriesInDomains(
                                                                            NSDocumentDirectory,
                                                                            NSUserDomainMask,
                                                                            YES);
            NSUUID *uuid = [NSUUID UUID];
            NSString *uniqueFileName = [uuid UUIDString];
            NSString *path = [NSString stringWithFormat:@"%@/%@.%@",
                              paths[0],
                              uniqueFileName,
                              fileExtension];

            [image writeToFile:path atomically:YES];

            NSLog(@"%@", path);
            self.onImageChange(@{
                @"data": base64,
                @"uri": path,
                @"mime": mimeType,
                              });
        });
    } else {
        // Call the normal paste action
        [[self backedTextInputView] paste:sender];
    }
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if (action == @selector(paste:) && [self pasteboardHasImages]) {
        return (BOOL)self.onImageChange;
    }

    return NO;
}
@end
