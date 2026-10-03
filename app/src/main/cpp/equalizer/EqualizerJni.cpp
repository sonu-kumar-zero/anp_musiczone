#include "EqualizerJni.h"

#include "Equalizer.h"

namespace {

    Equalizer* fromHandle(jlong handle) {
        return reinterpret_cast<Equalizer*>(handle);
    }

}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_musiczone_MainActivity_createEqualizer(
        JNIEnv* /* env */,
        jobject /* this */
) {
    auto* equalizer = new Equalizer();

    equalizer->configure(48000.0);

    return reinterpret_cast<jlong>(equalizer);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_MainActivity_destroyEqualizer(
        JNIEnv* /* env */,
        jobject /* this */,
        jlong handle
) {
    delete fromHandle(handle);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_MainActivity_setEqualizerBandGain(
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

    equalizer->setBandGain(
            static_cast<int>(band),
            gainDb
    );
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_example_musiczone_MainActivity_processEqualizer(
        JNIEnv* env,
        jobject /* this */,
        jlong handle,
        jfloatArray input
) {
    auto* equalizer = fromHandle(handle);

    if (equalizer == nullptr || input == nullptr) {
        return nullptr;
    }

    const jsize sampleCount =
            env->GetArrayLength(input);

    if (sampleCount <= 0 || sampleCount % 2 != 0) {
        return nullptr;
    }

    jfloat* samples =
            env->GetFloatArrayElements(input, nullptr);

    if (samples == nullptr) {
        return nullptr;
    }

    const int frameCount =
            static_cast<int>(sampleCount / 2);

    equalizer->process(
            samples,
            frameCount
    );

    jfloatArray output =
            env->NewFloatArray(sampleCount);

    if (output != nullptr) {
        env->SetFloatArrayRegion(
                output,
                0,
                sampleCount,
                samples
        );
    }

    env->ReleaseFloatArrayElements(
            input,
            samples,
            JNI_ABORT
    );

    return output;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_musiczone_MainActivity_resetEqualizer(
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