#import "WorkerSelfManager.h"
#include <stdlib.h>
#import "RCTBridge.h"
#import "RCTBridge+Private.h"
#import "RCTEventDispatcher.h"

@implementation WorkerSelfManager

RCT_EXPORT_MODULE();

@synthesize bridge = _bridge;
@synthesize parentBridge = _parentBridge;
@synthesize workerId = _workerId;

RCT_EXPORT_METHOD(postMessage: (NSString *)message)
{
  if (self.parentBridge == nil) {
    NSLog(@"No parent bridge defined - abord sending worker message");
    return;
  }

  NSString *eventName = [NSString stringWithFormat:@"Worker%i", self.workerId];

  [self.parentBridge.eventDispatcher sendAppEventWithName:eventName
                                               body:message];
}

@end