#include "EqualizerJni.h"

#include "Equalizer.h"

#include <android/log.h>

namespace {

    Equalizer* fromHandle(jlong handle) {
        return reinterpret_cast<Equalizer*>(handle);
    }

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_createEqualizer(
        JNIEnv* /* env */,
        jobject /* this */,
        jint sampleRate
) {
    auto* equalizer = new Equalizer();

    equalizer->configure(
            static_cast<double>(sampleRate)
    );

    return reinterpret_cast<jlong>(equalizer);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_destroyEqualizer(
        JNIEnv* /* env */,
        jobject /* this */,
        jlong handle
) {
    delete fromHandle(handle);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_setEqualizerBandGain(
        JNIEnv* /* env */,
        jobject /* this */,
        jlong handle,
        jint band,
        jdouble gainDb
) {
    auto* equalizer = fromHandle(handle);

    if (equalizer == nullptr) {
        return;
    }

    __android_log_print(
            ANDROID_LOG_INFO,
            "MusicZoneEQ",
            "setBandGain: band=%d gain=%.2f dB",
            static_cast<int>(band),
            static_cast<double>(gainDb)
    );

    equalizer->setBandGain(
            static_cast<int>(band),
            gainDb
    );
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_processEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jlong handle,
        jfloatArray input
) {
    auto* equalizer = fromHandle(handle);

    if (equalizer == nullptr || input == nullptr) {
        return;
    }

    const jsize sampleCount =
            env->GetArrayLength(input);

    if (sampleCount <= 0 || sampleCount % 2 != 0) {
        return;
    }

    jfloat* samples =
            env->GetFloatArrayElements(input, nullptr);

    if (samples == nullptr) {
        return;
    }

    const int frameCount =
            static_cast<int>(sampleCount / 2);

    equalizer->process(
            samples,
            frameCount
    );

    env->ReleaseFloatArrayElements(
            input,
            samples,
            0
    );
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_playback_equalizer_NativeEqualizerAudioProcessor_resetEqualizer(
        JNIEnv* /* env */,
        jobject /* this */,
        jlong handle
) {
    auto* equalizer = fromHandle(handle);

    if (equalizer == nullptr) {
        return;
    }

    equalizer->reset();
}

