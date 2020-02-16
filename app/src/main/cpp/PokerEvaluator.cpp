//
// Created by Thomas Bigger on 2020-02-15.
//

#include <stdlib.h>
#include <assert.h>
#include <jni.h>
#include <string.h>
#include "PokerEvaluator.h"

extern "C" {

// create buffer queue audio player
JNIEXPORT jint JNICALL
Java_com_twb_poker_PokerGameActivity_getRank(JNIEnv *env, jclass clazz, jint val1, jint val2) {
    jint addedResult = val1 + val2;
    return addedResult;
}

}