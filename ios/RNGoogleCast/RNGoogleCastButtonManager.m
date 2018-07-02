#import <GoogleCast/GoogleCast.h>
#import <React/RCTUIManager.h>
#import <React/RCTViewManager.h>

@interface RNGoogleCastButtonManager : RCTViewManager
@end

@implementation RNGoogleCastButtonManager

RCT_EXPORT_VIEW_PROPERTY(tintColor, UIColor)

RCT_EXPORT_MODULE()

- (UIView *)view {
  GCKUICastButton *castButton = [[GCKUICastButton alloc] init];
  return castButton;
}

RCT_EXPORT_METHOD(click:(nonnull NSNumber *)reactTag) {
  [self.bridge.uiManager addUIBlock:^(
                __unused RCTUIManager *uiManager,
                NSDictionary<NSNumber *, UIView *> *viewRegistry) {
        id view = viewRegistry[reactTag];
        if (![view isKindOfClass:[GCKUICastButton class]]) {
            RCTLogError(@"Invalid view returned from registry, expecting \
                        GCKUICastButton, got: %@", view);
        } else {
            // Simulate a click
            UIButton *btn = nil;
            for (UIView *buttonView in ((UIView *) view).subviews) {
                if ([buttonView isKindOfClass:[UIButton class]]) {
                    btn = (UIButton *) buttonView;
                    break;
                }
            }
            if (btn != nil) {
                [btn sendActionsForControlEvents:UIControlEventTouchUpInside];
            }
        }
    }];
}

@end
