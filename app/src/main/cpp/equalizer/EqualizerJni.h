#pragma once

#include <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_createEqualizer(
        JNIEnv *env,
        jobject /* this */,
        jint sampleRate
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_destroyEqualizer(
        JNIEnv *env,
        jobject /* this */,
        jlong handle
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_processEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jlong handle,
        jfloatArray input
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_setEqualizerBandGain(
        JNIEnv* env,
        jobject /* this */,
        jlong handle,
        jint band,
        jdouble gainDb
);

JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_resetEqualizer(
        JNIEnv *env,
        jobject /* this */,
        jlong handle
);

}