#import "WorkerManager.h"
#import "WorkerSelfManager.h"
#include <stdlib.h>
#import "RCTBridge.h"
#import "RCTBridge+Private.h"
#import "RCTEventDispatcher.h"
#import "RCTBundleURLProvider.h"

@implementation WorkerManager

@synthesize bridge = _bridge;

NSMutableDictionary *workers;

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(startWorker,
                 name: (NSString *)name
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
  if (workers == nil) {
    workers = [[NSMutableDictionary alloc] init];
  }

  int workerId = abs(arc4random());

  NSURL *workerURL = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:name fallbackResource:nil];
  NSLog(@"starting Worker %@", [workerURL absoluteString]);


   RCTBridge *workerBridge = [[RCTBridge alloc] initWithBundleURL:workerURL
                                            moduleProvider:nil
                                             launchOptions:nil];

  WorkerSelfManager *workerSelf = [workerBridge moduleForName:@"WorkerSelfManager"];
  [workerSelf setWorkerId:workerId];
  [workerSelf setParentBridge:self.bridge];


  [workers setObject:workerBridge forKey:[NSNumber numberWithInt:workerId]];
  resolve([NSNumber numberWithInt:workerId]);
}

RCT_EXPORT_METHOD(stopWorker:(int)workerId)
{
  if (workers == nil) {
    NSLog(@"Empty list of workers. abort stopping worker with id %i", workerId);
    return;
  }

  RCTBridge *workerBridge = workers[[NSNumber numberWithInt:workerId]];
  if (workerBridge == nil) {
    NSLog(@"Worker is NIl. abort stopping worker with id %i", workerId);
    return;
  }

  [workerBridge invalidate];
  [workers removeObjectForKey:[NSNumber numberWithInt:workerId]];
}

RCT_EXPORT_METHOD(postWorkerMessage: (int)workerId message:(NSString *)message)
{
  if (workers == nil) {
    NSLog(@"Empty list of workers. abort posting to worker with id %i", workerId);
    return;
  }

  RCTBridge *workerBridge = workers[[NSNumber numberWithInt:workerId]];
  if (workerBridge == nil) {
    NSLog(@"Worker is NIl. abort posting to worker with id %i", workerId);
    return;
  }

  [workerBridge.eventDispatcher sendAppEventWithName:@"WorkerMessage"
                                               body:message];
}

- (void)invalidate {
  if (workers == nil) {
    return;
  }

  for (NSNumber *workerId in workers) {
    RCTBridge *workerBridge = workers[workerId];
    [workerBridge invalidate];
  }

  [workers removeAllObjects];
  workers = nil;
}

@end
