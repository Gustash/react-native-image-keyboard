#import "RCTBaseTextInputView+MediaInput.h"
#include <objc/runtime.h>

@implementation RCTBaseTextInputView (MediaInput)
static char key;

- (void)setOnImageChange:(RCTDirectEventBlock)onImageChange
{
    objc_setAssociatedObject(self, &key, onImageChange, OBJC_ASSOCIATION_RETAIN);
}

- (RCTDirectEventBlock)onImageChange
{
    return objc_getAssociatedObject(self, &key);
}

@end
