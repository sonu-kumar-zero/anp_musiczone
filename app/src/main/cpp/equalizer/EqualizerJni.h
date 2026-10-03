#pragma once

#include <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_musiczone_MainActivity_createEqualizer(
        JNIEnv* env,
        jobject /* this */
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_MainActivity_destroyEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jlong handle
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_MainActivity_setEqualizerBandGain(
        JNIEnv* env,
        jobject /* this */,
        jlong handle,
        jint band,
        jdouble gainDb
);

JNIEXPORT jfloatArray JNICALL
Java_com_example_musiczone_MainActivity_processEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jlong handle,
        jfloatArray input
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_MainActivity_resetEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jlong handle
);
}