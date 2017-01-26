
#ifndef WorkerSelfManager_h
#define WorkerSelfManager_h

#import <React/RCTBridgeModule.h>

@interface WorkerSelfManager : NSObject <RCTBridgeModule>
@property int workerId;
@property RCTBridge *parentBridge;
@end

#endif /* WorkerSelfManager_h */
