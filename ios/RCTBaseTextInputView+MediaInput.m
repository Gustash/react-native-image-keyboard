#import "RCTBaseTextInputView+MediaInput.h"

#import "React/RCTBridge.h"

@implementation RCTBaseTextInputView (MediaInput)

- (void)paste:(id)sender{

    UIImage *image = [UIPasteboard generalPasteboard].image;

    if (image) {
        NSData *png = UIImagePNGRepresentation(image);
        NSString *base64 = [png base64EncodedStringWithOptions:0];

        NSArray<NSString*> *paths = NSSearchPathForDirectoriesInDomains(
                                                                        NSDocumentDirectory,
                                                                        NSUserDomainMask,
                                                                        YES);
        NSString *path = [NSString stringWithFormat:@"%@/%@", paths[0], @"image.png"];

        [png writeToFile:path atomically:YES];

        NSLog(@"%@", path);

        NSTextAttachment *textAttachment = [[NSTextAttachment alloc] init];
        textAttachment.image = image;
        NSAttributedString *imageString = [NSAttributedString attributedStringWithAttachment:textAttachment];
        self.attributedText = imageString;
    } else {
        // Call the normal paste action
        [super paste:sender];
    }
}

- (BOOL)canPerformAction:(SEL)action withSender:(id)sender
{
    if (action == @selector(paste:) && [UIPasteboard generalPasteboard].image)
        return YES;
    else
        return [super canPerformAction:action withSender:sender];
}

@end
