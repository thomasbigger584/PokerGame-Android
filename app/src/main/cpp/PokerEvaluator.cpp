#include <jni.h>
#include "SevenEval.h"

extern "C" {

JNIEXPORT jint JNICALL
Java_com_twb_poker_eval_SevenCardHandEvaluator_getRank(JNIEnv *env, jclass clazz, jint i, jint j,
                                                       jint k,
                                                       jint m, jint n, jint p, jint q) {
    return SevenEval::GetRank(i, j, k, m, n, p, q);
}

}
